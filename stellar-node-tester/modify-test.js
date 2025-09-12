import axios from 'axios';
import chalk from 'chalk';

const API_URL = 'http://localhost:8090/api';

// Test cases for entity modifications
const testCases = [
  // 1. Simple INSERT
  {
    name: 'Insert new category',
    request: {
      entityName: 'category',
      operation: 'INSERT',
      data: {
        CategoryName: 'Electronics',
        Description: 'Electronic devices and accessories'
      }
    }
  },
  
  // 2. Nested INSERT - Category with Products
  {
    name: 'Insert category with products',
    request: {
      entityName: 'category',
      operation: 'INSERT',
      data: {
        CategoryName: 'Home Appliances',
        Description: 'Kitchen and home appliances'
      },
      include: [
        {
          entityName: 'product',
          operation: 'INSERT',
          data: {
            ProductName: 'Microwave Oven',
            UnitPrice: 199.99,
            UnitsInStock: 50
          }
        },
        {
          entityName: 'product',
          operation: 'INSERT',
          data: {
            ProductName: 'Coffee Maker',
            UnitPrice: 89.99,
            UnitsInStock: 30
          }
        }
      ]
    }
  },
  
  // 3. Deep nested INSERT - Customer with Order and OrderDetails
  {
    name: 'Insert customer with order and order details',
    request: {
      entityName: 'customer',
      operation: 'INSERT',
      data: {
        CustomerID: 'TESTC',
        CompanyName: 'Test Company Inc',
        ContactName: 'John Doe',
        City: 'New York',
        Country: 'USA'
      },
      include: [
        {
          entityName: 'salesorder',
          operation: 'INSERT',
          data: {
            OrderDate: '2024-01-15',
            ShipCity: 'New York',
            ShipCountry: 'USA',
            EmployeeID: 1,
            ShipVia: 1
          },
          include: [
            {
              entityName: 'orderdetail',
              operation: 'INSERT',
              data: {
                ProductID: 1,
                UnitPrice: 18.00,
                Quantity: 10,
                Discount: 0.05
              }
            },
            {
              entityName: 'orderdetail',
              operation: 'INSERT',
              data: {
                ProductID: 2,
                UnitPrice: 19.00,
                Quantity: 5,
                Discount: 0
              }
            }
          ]
        }
      ]
    }
  },
  
  // 4. UPDATE operation
  {
    name: 'Update product price',
    request: {
      entityName: 'product',
      operation: 'UPDATE',
      data: {
        UnitPrice: 25.99,
        UnitsInStock: 100
      },
      criteria: {
        ProductID: 1
      }
    }
  },
  
  // 5. UPDATE with nested operations
  {
    name: 'Update category and add new product',
    request: {
      entityName: 'category',
      operation: 'UPDATE',
      data: {
        Description: 'Updated description for beverages'
      },
      criteria: {
        CategoryID: 1
      },
      include: [
        {
          entityName: 'product',
          operation: 'INSERT',
          data: {
            ProductName: 'New Beverage Product',
            UnitPrice: 4.99,
            UnitsInStock: 200
          }
        }
      ]
    }
  },
  
  // 6. DELETE operation
  {
    name: 'Delete test customer',
    request: {
      entityName: 'customer',
      operation: 'DELETE',
      criteria: {
        CustomerID: 'TESTC'
      }
    }
  },
  
  // 7. Invalid relationship test (should fail)
  {
    name: 'Invalid relationship - Category to Employee',
    request: {
      entityName: 'category',
      operation: 'INSERT',
      data: {
        CategoryName: 'Test Category',
        Description: 'This should fail'
      },
      include: [
        {
          entityName: 'employee', // Invalid relationship
          operation: 'INSERT',
          data: {
            FirstName: 'John',
            LastName: 'Doe'
          }
        }
      ]
    },
    expectError: true
  }
];

// Execute a single test
async function executeTest(testCase, index) {
  console.log(chalk.cyan(`\nTest ${index + 1}: ${testCase.name}`));
  console.log(chalk.gray('Request:'), JSON.stringify(testCase.request, null, 2));
  
  try {
    const response = await axios.post(`${API_URL}/modify`, testCase.request, {
      validateStatus: () => true // Don't throw on HTTP errors
    });
    
    if (response.status === 200) {
      if (testCase.expectError) {
        console.log(chalk.red('✗ Expected error but got success'));
        return false;
      }
      console.log(chalk.green('✓ Success'));
      console.log(chalk.gray('Response:'), JSON.stringify(response.data, null, 2));
      
      // Check cache stats
      if (response.data.cacheStats) {
        console.log(chalk.yellow('Cache Stats:'));
        console.log(`  Hierarchy: ${response.data.hierarchyKey}`);
        console.log(`  Usage Count: ${response.data.cacheStats.usageCount}`);
      }
      return true;
    } else {
      if (testCase.expectError) {
        console.log(chalk.green('✓ Expected error received'));
        console.log(chalk.gray('Error:'), response.data.error);
        return true;
      }
      console.log(chalk.red('✗ Error:'), response.data.error);
      return false;
    }
  } catch (error) {
    console.log(chalk.red('✗ Request failed:'), error.message);
    return testCase.expectError || false;
  }
}

// Check cache statistics
async function checkCacheStats() {
  try {
    const response = await axios.get(`${API_URL}/cache/stats`);
    console.log(chalk.blue('\n📊 Cache Statistics:'));
    console.log(chalk.gray(JSON.stringify(response.data, null, 2)));
  } catch (error) {
    console.log(chalk.red('Failed to get cache stats:'), error.message);
  }
}

// Clear cache
async function clearCache() {
  try {
    const response = await axios.delete(`${API_URL}/cache/clear`);
    console.log(chalk.yellow('\n🗑️  Cache cleared'));
  } catch (error) {
    console.log(chalk.red('Failed to clear cache:'), error.message);
  }
}

// Main test runner
async function runTests() {
  console.log(chalk.bold.blue('\n🚀 Entity Modification Test Suite'));
  console.log(chalk.gray('═'.repeat(60)));
  
  // Check if server is running
  try {
    await axios.get(`${API_URL}/health`);
  } catch (error) {
    console.log(chalk.red('❌ Server not running on port 8090'));
    console.log(chalk.yellow('Please start the server with: cd stellar-rest && ./mvnw quarkus:dev'));
    process.exit(1);
  }
  
  // Clear cache before starting
  await clearCache();
  
  let passed = 0;
  let failed = 0;
  
  // Run all tests
  for (let i = 0; i < testCases.length; i++) {
    const result = await executeTest(testCases[i], i);
    if (result) passed++;
    else failed++;
    
    // Small delay between tests
    await new Promise(resolve => setTimeout(resolve, 500));
  }
  
  // Show final cache statistics
  await checkCacheStats();
  
  // Summary
  console.log(chalk.gray('\n' + '═'.repeat(60)));
  console.log(chalk.bold.white('TEST SUMMARY'));
  console.log(chalk.green(`✓ Passed: ${passed}`));
  console.log(chalk.red(`✗ Failed: ${failed}`));
  console.log(chalk.cyan(`Total: ${testCases.length}`));
  
  if (failed === 0) {
    console.log(chalk.bold.green('\n🎉 All tests passed!'));
  } else {
    console.log(chalk.bold.yellow(`\n⚠️  ${failed} tests failed`));
  }
}

// Performance test - check caching effect
async function performanceTest() {
  console.log(chalk.bold.blue('\n⚡ Performance Test - Caching Effect'));
  console.log(chalk.gray('═'.repeat(60)));
  
  const testRequest = {
    entityName: 'category',
    operation: 'INSERT',
    data: {
      CategoryName: `PerfTest_${Date.now()}`,
      Description: 'Performance test category'
    },
    include: [
      {
        entityName: 'product',
        operation: 'INSERT',
        data: {
          ProductName: 'PerfTest Product',
          UnitPrice: 99.99,
          UnitsInStock: 10
        }
      }
    ]
  };
  
  // Clear cache first
  await clearCache();
  
  // First call - builds hierarchy
  console.log(chalk.yellow('\nFirst call (building hierarchy):'));
  let start = Date.now();
  await axios.post(`${API_URL}/modify`, testRequest);
  let duration1 = Date.now() - start;
  console.log(`Duration: ${duration1}ms`);
  
  // Modify data to avoid duplicate key errors
  testRequest.data.CategoryName = `PerfTest_${Date.now()}`;
  
  // Second call - uses cache
  console.log(chalk.yellow('\nSecond call (using cache):'));
  start = Date.now();
  await axios.post(`${API_URL}/modify`, testRequest);
  let duration2 = Date.now() - start;
  console.log(`Duration: ${duration2}ms`);
  
  // Modify data again
  testRequest.data.CategoryName = `PerfTest_${Date.now()}`;
  
  // Third call - also uses cache
  console.log(chalk.yellow('\nThird call (using cache):'));
  start = Date.now();
  await axios.post(`${API_URL}/modify`, testRequest);
  let duration3 = Date.now() - start;
  console.log(`Duration: ${duration3}ms`);
  
  console.log(chalk.green(`\n✨ Cache speedup: ${((duration1 - duration2) / duration1 * 100).toFixed(1)}%`));
  console.log(chalk.gray('Note: Subsequent calls are faster due to cached hierarchy metadata'));
}

// Main
async function main() {
  const args = process.argv.slice(2);
  
  if (args.includes('--perf')) {
    await performanceTest();
  } else {
    await runTests();
  }
}

main().catch(console.error);