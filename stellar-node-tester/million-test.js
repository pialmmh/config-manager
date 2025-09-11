import axios from 'axios';
import chalk from 'chalk';
import fs from 'fs';

const API_URL = 'http://localhost:8090/api/query';

// Valid Northwind entity relationships
const validRelationships = {
  category: ['product'],
  product: ['orderdetail', 'category', 'supplier'],
  customer: ['salesorder'],
  salesorder: ['orderdetail', 'customer', 'employee', 'shipper'],
  orderdetail: ['product', 'salesorder'],
  employee: ['salesorder'],
  shipper: ['salesorder'],
  supplier: ['product']
};

// Entity counts for realistic pagination
const entityCounts = {
  category: 8,
  product: 77,
  customer: 91,
  salesorder: 830,
  orderdetail: 2155,
  employee: 9,
  shipper: 3,
  supplier: 29
};

const entities = Object.keys(validRelationships);

// Statistics tracking
let stats = {
  total: 0,
  successful: 0,
  failed: 0,
  errors: {},
  startTime: Date.now(),
  depthDistribution: {0: 0, 1: 0, 2: 0, 3: 0, 4: 0, 5: 0},
  entityDistribution: {},
  avgResponseTime: 0,
  totalResponseTime: 0
};

// Initialize entity distribution
entities.forEach(e => stats.entityDistribution[e] = 0);

// Generate random criteria
function generateCriteria(entity, probability = 0.3) {
  if (Math.random() > probability) return undefined;
  
  const criteriaTemplates = {
    category: [
      { CategoryID: Math.ceil(Math.random() * 8) },
      { CategoryName: ['Beverages', 'Dairy Products', 'Seafood'][Math.floor(Math.random() * 3)] }
    ],
    product: [
      { ProductID: Math.ceil(Math.random() * 77) },
      { UnitPrice: { $gte: Math.random() * 50 } },
      { UnitsInStock: { $lte: Math.random() * 100 } },
      { CategoryID: Math.ceil(Math.random() * 8) }
    ],
    customer: [
      { CustomerID: `CUSTOM${Math.floor(Math.random() * 91)}` },
      { Country: ['USA', 'Germany', 'France', 'UK'][Math.floor(Math.random() * 4)] },
      { City: ['London', 'Paris', 'Berlin', 'Madrid'][Math.floor(Math.random() * 4)] }
    ],
    salesorder: [
      { OrderID: Math.ceil(Math.random() * 830) },
      { CustomerID: `CUSTOM${Math.floor(Math.random() * 91)}` },
      { EmployeeID: Math.ceil(Math.random() * 9) },
      { ShipCountry: ['USA', 'Germany', 'France'][Math.floor(Math.random() * 3)] }
    ],
    orderdetail: [
      { OrderID: Math.ceil(Math.random() * 830) },
      { ProductID: Math.ceil(Math.random() * 77) },
      { Quantity: { $gte: Math.random() * 50 } },
      { UnitPrice: { $lte: Math.random() * 100 } }
    ],
    employee: [
      { EmployeeID: Math.ceil(Math.random() * 9) },
      { Title: ['Sales Representative', 'Sales Manager'][Math.floor(Math.random() * 2)] },
      { Country: ['USA', 'UK'][Math.floor(Math.random() * 2)] }
    ],
    shipper: [
      { ShipperID: Math.ceil(Math.random() * 3) },
      { CompanyName: ['Speedy Express', 'United Package', 'Federal Shipping'][Math.floor(Math.random() * 3)] }
    ],
    supplier: [
      { SupplierID: Math.ceil(Math.random() * 29) },
      { Country: ['USA', 'UK', 'Japan', 'Germany'][Math.floor(Math.random() * 4)] },
      { City: ['London', 'Tokyo', 'Berlin'][Math.floor(Math.random() * 3)] }
    ]
  };
  
  const templates = criteriaTemplates[entity];
  return templates[Math.floor(Math.random() * templates.length)];
}

// Generate random pagination
function generatePage(entity, aggressive = false) {
  if (Math.random() > 0.7) return undefined; // 30% no pagination
  
  const maxCount = entityCounts[entity];
  const limit = aggressive ? 
    Math.ceil(Math.random() * 100) : // 1-100 for aggressive
    Math.ceil(Math.random() * Math.min(50, maxCount)); // reasonable limits
  
  const offset = Math.floor(Math.random() * Math.max(0, maxCount - limit));
  
  return { limit, offset };
}

// Generate valid nested query with controlled depth
function generateNestedQuery(parentEntity, currentDepth, maxDepth, visitedEntities = new Set()) {
  if (currentDepth >= maxDepth) return [];
  
  const children = validRelationships[parentEntity];
  if (!children || children.length === 0) return [];
  
  // Randomly select how many children to include (0-2 typically)
  const numChildren = Math.random() < 0.4 ? 0 : 
                     Math.random() < 0.7 ? 1 : 
                     Math.min(2, children.length);
  
  if (numChildren === 0) return [];
  
  const includes = [];
  const availableChildren = children.filter(c => !visitedEntities.has(c));
  
  for (let i = 0; i < numChildren && i < availableChildren.length; i++) {
    const childEntity = availableChildren[Math.floor(Math.random() * availableChildren.length)];
    if (!childEntity) continue;
    
    const newVisited = new Set(visitedEntities);
    newVisited.add(childEntity);
    
    const childQuery = {
      kind: childEntity
    };
    
    // Add criteria with decreasing probability at deeper levels
    const criteria = generateCriteria(childEntity, 0.3 / (currentDepth + 1));
    if (criteria) childQuery.criteria = criteria;
    
    // Add pagination
    const page = generatePage(childEntity, currentDepth > 2);
    if (page) childQuery.page = page;
    
    // Recursively add nested queries
    const nested = generateNestedQuery(childEntity, currentDepth + 1, maxDepth, newVisited);
    if (nested.length > 0) childQuery.include = nested;
    
    includes.push(childQuery);
  }
  
  return includes;
}

// Generate a complete query
function generateQuery(targetDepth = null) {
  const rootEntity = entities[Math.floor(Math.random() * entities.length)];
  stats.entityDistribution[rootEntity]++;
  
  // Determine max depth (0-5)
  const maxDepth = targetDepth !== null ? targetDepth : 
                   Math.random() < 0.2 ? 0 :  // 20% simple queries
                   Math.random() < 0.4 ? 1 :  // 20% one level
                   Math.random() < 0.6 ? 2 :  // 20% two levels
                   Math.random() < 0.8 ? 3 :  // 20% three levels
                   Math.random() < 0.95 ? 4 : // 15% four levels
                   5;                          // 5% five levels
  
  stats.depthDistribution[Math.min(maxDepth, 5)]++;
  
  const query = {
    kind: rootEntity
  };
  
  // Add root criteria
  const criteria = generateCriteria(rootEntity, 0.4);
  if (criteria) query.criteria = criteria;
  
  // Add root pagination
  const page = generatePage(rootEntity);
  if (page) query.page = page;
  
  // Add nested queries
  if (maxDepth > 0) {
    const visitedEntities = new Set([rootEntity]);
    const includes = generateNestedQuery(rootEntity, 0, maxDepth, visitedEntities);
    if (includes.length > 0) query.include = includes;
  }
  
  return query;
}

// Execute a single query
async function executeQuery(query, index) {
  const startTime = Date.now();
  
  try {
    const response = await axios.post(API_URL, query, {
      timeout: 5000,
      validateStatus: () => true // Don't throw on HTTP errors
    });
    
    const responseTime = Date.now() - startTime;
    stats.totalResponseTime += responseTime;
    stats.avgResponseTime = stats.totalResponseTime / (stats.successful + stats.failed + 1);
    
    if (response.data.success) {
      stats.successful++;
      return { 
        success: true, 
        rows: response.data.count || 0,
        time: responseTime 
      };
    } else {
      stats.failed++;
      const error = response.data.error || 'Unknown error';
      stats.errors[error] = (stats.errors[error] || 0) + 1;
      return { 
        success: false, 
        error: error,
        time: responseTime 
      };
    }
  } catch (error) {
    stats.failed++;
    const errorMsg = error.code === 'ECONNREFUSED' ? 'Server not running' : error.message;
    stats.errors[errorMsg] = (stats.errors[errorMsg] || 0) + 1;
    return { 
      success: false, 
      error: errorMsg,
      time: Date.now() - startTime 
    };
  }
}

// Progress bar
function updateProgress(current, total) {
  const percentage = ((current / total) * 100).toFixed(2);
  const barLength = 40;
  const filled = Math.round((current / total) * barLength);
  const bar = '█'.repeat(filled) + '░'.repeat(barLength - filled);
  
  const elapsed = (Date.now() - stats.startTime) / 1000;
  const rate = current / elapsed;
  const eta = (total - current) / rate;
  
  process.stdout.write(
    `\r${chalk.cyan(bar)} ${percentage}% | ` +
    `${chalk.green(stats.successful)} ✓ ${chalk.red(stats.failed)} ✗ | ` +
    `${current.toLocaleString()}/${total.toLocaleString()} | ` +
    `${rate.toFixed(0)} q/s | ` +
    `ETA: ${Math.floor(eta / 60)}m ${Math.floor(eta % 60)}s`
  );
}

// Execute queries in batches
async function executeBatch(queries, startIndex, batchSize = 100) {
  const promises = [];
  
  for (let i = 0; i < queries.length && i < batchSize; i++) {
    promises.push(executeQuery(queries[i], startIndex + i));
  }
  
  return Promise.all(promises);
}

// Main execution
async function runMillionTest() {
  console.log(chalk.bold.blue('\n🚀 Million Query Test Suite'));
  console.log(chalk.gray('═'.repeat(80)));
  console.log(chalk.yellow('Generating 1,000,000 nested queries with varying parameters...'));
  
  const TARGET = 1_000_000;
  const BATCH_SIZE = 100; // Process 100 queries concurrently
  
  // Write header to log file
  const logStream = fs.createWriteStream('million-test-results.log');
  logStream.write(`Million Query Test Started: ${new Date().toISOString()}\n`);
  logStream.write('═'.repeat(80) + '\n\n');
  
  stats.total = TARGET;
  stats.startTime = Date.now();
  
  // Process in chunks to avoid memory issues
  const CHUNK_SIZE = 10000;
  
  for (let chunk = 0; chunk < TARGET; chunk += CHUNK_SIZE) {
    const chunkSize = Math.min(CHUNK_SIZE, TARGET - chunk);
    
    // Generate queries for this chunk
    const queries = [];
    for (let i = 0; i < chunkSize; i++) {
      queries.push(generateQuery());
    }
    
    // Execute in batches
    for (let i = 0; i < queries.length; i += BATCH_SIZE) {
      const batch = queries.slice(i, Math.min(i + BATCH_SIZE, queries.length));
      const results = await executeBatch(batch, chunk + i, BATCH_SIZE);
      
      // Log failures to file
      results.forEach((result, index) => {
        if (!result.success && Math.random() < 0.01) { // Sample 1% of failures
          logStream.write(`Query ${chunk + i + index} failed: ${result.error}\n`);
        }
      });
      
      updateProgress(chunk + i + batch.length, TARGET);
      
      // Small delay to avoid overwhelming the server
      if ((chunk + i) % 1000 === 0) {
        await new Promise(resolve => setTimeout(resolve, 10));
      }
    }
  }
  
  // Final statistics
  const duration = (Date.now() - stats.startTime) / 1000;
  const qps = stats.total / duration;
  
  console.log('\n\n' + chalk.bold.green('═'.repeat(80)));
  console.log(chalk.bold.white('TEST COMPLETE'));
  console.log(chalk.green('═'.repeat(80)));
  
  console.log(chalk.bold('\n📊 Summary Statistics:'));
  console.log(chalk.cyan(`  Total Queries: ${stats.total.toLocaleString()}`));
  console.log(chalk.green(`  Successful: ${stats.successful.toLocaleString()} (${(stats.successful/stats.total*100).toFixed(2)}%)`));
  console.log(chalk.red(`  Failed: ${stats.failed.toLocaleString()} (${(stats.failed/stats.total*100).toFixed(2)}%)`));
  console.log(chalk.yellow(`  Duration: ${duration.toFixed(2)}s`));
  console.log(chalk.magenta(`  Throughput: ${qps.toFixed(2)} queries/second`));
  console.log(chalk.blue(`  Avg Response Time: ${stats.avgResponseTime.toFixed(2)}ms`));
  
  console.log(chalk.bold('\n📈 Depth Distribution:'));
  Object.entries(stats.depthDistribution).forEach(([depth, count]) => {
    const pct = (count / stats.total * 100).toFixed(2);
    console.log(`  Level ${depth}: ${count.toLocaleString()} (${pct}%)`);
  });
  
  console.log(chalk.bold('\n🎯 Entity Distribution:'));
  Object.entries(stats.entityDistribution)
    .sort((a, b) => b[1] - a[1])
    .forEach(([entity, count]) => {
      const pct = (count / stats.total * 100).toFixed(2);
      console.log(`  ${entity}: ${count.toLocaleString()} (${pct}%)`);
    });
  
  if (Object.keys(stats.errors).length > 0) {
    console.log(chalk.bold('\n❌ Top Error Types:'));
    Object.entries(stats.errors)
      .sort((a, b) => b[1] - a[1])
      .slice(0, 10)
      .forEach(([error, count]) => {
        console.log(`  ${error}: ${count.toLocaleString()}`);
      });
  }
  
  // Write summary to log
  logStream.write('\n' + '═'.repeat(80) + '\n');
  logStream.write('TEST SUMMARY\n');
  logStream.write('═'.repeat(80) + '\n');
  logStream.write(`Total: ${stats.total}\n`);
  logStream.write(`Successful: ${stats.successful}\n`);
  logStream.write(`Failed: ${stats.failed}\n`);
  logStream.write(`Success Rate: ${(stats.successful/stats.total*100).toFixed(2)}%\n`);
  logStream.write(`Duration: ${duration.toFixed(2)}s\n`);
  logStream.write(`Throughput: ${qps.toFixed(2)} q/s\n`);
  logStream.end();
  
  console.log(chalk.gray('\nDetailed results written to: million-test-results.log'));
}

// Check if server is running
async function checkServer() {
  try {
    await axios.get('http://localhost:8090/api/health');
    return true;
  } catch (error) {
    console.log(chalk.red('❌ Server not running on port 8090'));
    console.log(chalk.yellow('Please start the server with: cd stellar-rest && ./mvnw quarkus:dev'));
    return false;
  }
}

// Main
async function main() {
  const serverRunning = await checkServer();
  if (!serverRunning) {
    process.exit(1);
  }
  
  await runMillionTest();
}

main().catch(console.error);