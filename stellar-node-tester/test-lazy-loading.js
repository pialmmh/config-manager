import axios from 'axios';

const BASE_URL = 'http://localhost:8090';

// Test queries with lazy loading
const lazyQueries = [
  {
    name: 'Customer with lazy orders',
    query: {
      kind: 'customer',
      criteria: { CustomerID: ['ALFKI', 'ANATR'] },
      page: { limit: 2 },
      include: [{
        kind: 'salesorder',
        lazy: true,
        page: { limit: 5 },
        include: [{
          kind: 'orderdetail',
          page: { limit: 3 }
        }]
      }]
    }
  },
  {
    name: 'Category with lazy products',
    query: {
      kind: 'category',
      page: { limit: 3 },
      include: [{
        kind: 'product',
        lazy: true,
        page: { limit: 10 }
      }]
    }
  },
  {
    name: 'Mixed lazy and eager loading',
    query: {
      kind: 'customer',
      page: { limit: 2 },
      include: [
        {
          kind: 'salesorder',
          page: { limit: 2 },
          include: [{
            kind: 'orderdetail',
            lazy: true,
            page: { limit: 5 }
          }]
        }
      ]
    }
  }
];

async function testLazyQuery(testCase) {
  console.log(`\n${'='.repeat(60)}`);
  console.log(`Test: ${testCase.name}`);
  console.log(`${'='.repeat(60)}`);
  
  try {
    // Step 1: Execute main query with lazy placeholders
    console.log('\n1. Executing main query...');
    const mainResponse = await axios.post(`${BASE_URL}/api/lazy/query`, testCase.query);
    
    if (!mainResponse.data.success) {
      throw new Error(mainResponse.data.error);
    }
    
    const { data, lazyLoaders } = mainResponse.data;
    
    console.log(`   ✓ Main query returned ${data.length} rows`);
    
    if (lazyLoaders && Object.keys(lazyLoaders).length > 0) {
      console.log(`   ✓ Found ${Object.keys(lazyLoaders).length} lazy loaders:`);
      
      for (const [path, loader] of Object.entries(lazyLoaders)) {
        console.log(`     - ${path}: ${loader.kind} (key: ${loader.key})`);
      }
      
      // Step 2: Test lazy loading for each placeholder
      console.log('\n2. Testing lazy loading...');
      
      for (const [path, loader] of Object.entries(lazyLoaders)) {
        // Get parent IDs from the first row of main data
        const parentIds = {};
        if (data.length > 0) {
          const firstRow = data[0];
          // Extract ID field based on entity type
          const idField = getIdField(testCase.query.kind);
          if (firstRow[idField]) {
            parentIds[idField] = firstRow[idField];
          }
        }
        
        console.log(`\n   Loading: ${path}`);
        console.log(`   Parent IDs: ${JSON.stringify(parentIds)}`);
        
        try {
          const lazyResponse = await axios.post(
            `${BASE_URL}/api/lazy/load/${loader.key}`,
            parentIds
          );
          
          if (lazyResponse.data.success) {
            console.log(`   ✓ Loaded ${lazyResponse.data.data.length} rows for ${path}`);
          } else {
            console.log(`   ✗ Failed to load ${path}: ${lazyResponse.data.error}`);
          }
        } catch (error) {
          console.log(`   ✗ Error loading ${path}: ${error.message}`);
        }
      }
    } else {
      console.log('   No lazy loaders found');
    }
    
    return { success: true, dataCount: data.length, lazyCount: Object.keys(lazyLoaders || {}).length };
    
  } catch (error) {
    console.log(`\n✗ Test failed: ${error.message}`);
    return { success: false, error: error.message };
  }
}

function getIdField(kind) {
  const idFields = {
    customer: 'CustomerID',
    category: 'CategoryID',
    product: 'ProductID',
    salesorder: 'OrderID',
    orderdetail: 'OrderID',
    employee: 'EmployeeID',
    shipper: 'ShipperID',
    supplier: 'SupplierID'
  };
  return idFields[kind] || 'id';
}

async function testNonLazyComparison() {
  console.log('\n' + '='.repeat(60));
  console.log('Performance Comparison: Lazy vs Non-Lazy Loading');
  console.log('='.repeat(60));
  
  // Non-lazy query
  const nonLazyQuery = {
    kind: 'customer',
    page: { limit: 10 },
    include: [{
      kind: 'salesorder',
      page: { limit: 5 },
      include: [{
        kind: 'orderdetail',
        page: { limit: 3 }
      }]
    }]
  };
  
  // Lazy query
  const lazyQuery = {
    kind: 'customer',
    page: { limit: 10 },
    include: [{
      kind: 'salesorder',
      lazy: true,
      page: { limit: 5 },
      include: [{
        kind: 'orderdetail',
        page: { limit: 3 }
      }]
    }]
  };
  
  // Test non-lazy
  console.log('\nTesting NON-LAZY query...');
  const nonLazyStart = Date.now();
  const nonLazyResponse = await axios.post(`${BASE_URL}/api/query`, nonLazyQuery);
  const nonLazyTime = Date.now() - nonLazyStart;
  console.log(`✓ Non-lazy query completed in ${nonLazyTime}ms`);
  console.log(`  Returned ${nonLazyResponse.data.data?.length || 0} rows`);
  
  // Test lazy
  console.log('\nTesting LAZY query...');
  const lazyStart = Date.now();
  const lazyResponse = await axios.post(`${BASE_URL}/api/lazy/query`, lazyQuery);
  const lazyTime = Date.now() - lazyStart;
  console.log(`✓ Lazy query (initial) completed in ${lazyTime}ms`);
  console.log(`  Returned ${lazyResponse.data.data?.length || 0} rows`);
  console.log(`  Lazy loaders: ${Object.keys(lazyResponse.data.lazyLoaders || {}).length}`);
  
  // Compare
  console.log('\n' + '-'.repeat(40));
  console.log('Comparison Results:');
  console.log(`Initial load time improvement: ${((nonLazyTime - lazyTime) / nonLazyTime * 100).toFixed(1)}%`);
  console.log(`Non-lazy: ${nonLazyTime}ms (all data loaded)`);
  console.log(`Lazy: ${lazyTime}ms (main data only)`);
  console.log('\nLazy loading allows:');
  console.log('  - Faster initial page load');
  console.log('  - Load nested data only when needed');
  console.log('  - Reduced memory usage for unused data');
}

async function runAllTests() {
  console.log('LAZY LOADING TEST SUITE');
  console.log('Testing lazy loading functionality\n');
  
  // Check if server is running
  try {
    await axios.get(`${BASE_URL}/api/health`);
    console.log('✓ Server is running\n');
  } catch (error) {
    console.error('✗ Server is not running on port 8090');
    console.error('  Please start the Quarkus server first');
    return;
  }
  
  // Run lazy loading tests
  const results = [];
  for (const testCase of lazyQueries) {
    const result = await testLazyQuery(testCase);
    results.push({ name: testCase.name, ...result });
  }
  
  // Run performance comparison
  await testNonLazyComparison();
  
  // Summary
  console.log('\n' + '='.repeat(60));
  console.log('TEST SUMMARY');
  console.log('='.repeat(60));
  
  const successful = results.filter(r => r.success).length;
  console.log(`Tests passed: ${successful}/${results.length}`);
  
  results.forEach(result => {
    const status = result.success ? '✓' : '✗';
    const details = result.success 
      ? `${result.dataCount} rows, ${result.lazyCount} lazy loaders`
      : result.error;
    console.log(`${status} ${result.name}: ${details}`);
  });
  
  // Get lazy cache stats
  console.log('\n' + '-'.repeat(40));
  console.log('Lazy Cache Statistics:');
  try {
    const statsResponse = await axios.get(`${BASE_URL}/api/lazy/cache/stats`);
    console.log(`Total cached keys: ${statsResponse.data.totalKeys}`);
    if (statsResponse.data.keys && statsResponse.data.keys.length > 0) {
      console.log('Cached keys:', statsResponse.data.keys.slice(0, 5).join(', '));
    }
  } catch (error) {
    console.log('Could not fetch cache stats');
  }
}

// Run the tests
runAllTests().catch(console.error);