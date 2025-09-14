import axios from 'axios';

// Configuration for both servers
const SERVERS = {
    springBoot: {
        name: 'Spring Boot',
        baseUrl: 'http://localhost:8091/api',
        port: 8091
    },
    quarkus: {
        name: 'Quarkus',
        baseUrl: 'http://localhost:8090/api',
        port: 8090
    }
};

// Valid entity relationships for queries
const entities = ['category', 'product', 'customer', 'salesorder', 'employee', 'shipper', 'supplier'];
const validRelationships = {
    'category': ['product'],
    'product': ['orderdetail'],
    'customer': ['salesorder'],
    'salesorder': ['orderdetail'],
    'employee': ['salesorder'],
    'shipper': ['salesorder'],
    'supplier': ['product'],
    'orderdetail': []
};

// Generate a valid random query
function generateRandomQuery() {
    const rootEntity = entities[Math.floor(Math.random() * entities.length)];
    const depth = Math.floor(Math.random() * 4); // 0-3 levels deep
    
    function buildLevel(entity, currentDepth) {
        const query = {
            kind: entity,
            limit: Math.floor(Math.random() * 10) + 1,
            offset: Math.floor(Math.random() * 50)
        };
        
        // Add filter conditions randomly
        if (Math.random() > 0.5) {
            query.filter = {
                conditions: [{
                    field: entity === 'product' ? 'UnitsInStock' : 
                           entity === 'salesorder' ? 'OrderID' : 'id',
                    operator: '>',
                    value: Math.floor(Math.random() * 100)
                }]
            };
        }
        
        // Add nested includes
        if (currentDepth < depth) {
            const possibleChildren = validRelationships[entity];
            if (possibleChildren && possibleChildren.length > 0) {
                const child = possibleChildren[Math.floor(Math.random() * possibleChildren.length)];
                query.include = [buildLevel(child, currentDepth + 1)];
            }
        }
        
        return query;
    }
    
    return buildLevel(rootEntity, 0);
}

// Execute query
async function executeQuery(server, query) {
    try {
        const response = await axios.post(`${server.baseUrl}/query`, query, {
            headers: { 'Content-Type': 'application/json' },
            timeout: 30000
        });
        return { success: true, data: response.data };
    } catch (error) {
        return { 
            success: false, 
            error: error.response?.data?.error || error.message 
        };
    }
}

// Run million query test for a specific server
async function runMillionTest(server) {
    console.log(`\n${'='.repeat(70)}`);
    console.log(`MILLION QUERY TEST - ${server.name} (Port ${server.port})`);
    console.log(`${'='.repeat(70)}\n`);
    
    const totalQueries = 1000000;
    const batchSize = 100;
    const totalBatches = totalQueries / batchSize;
    
    let successCount = 0;
    let errorCount = 0;
    let totalDuration = 0;
    const startTime = Date.now();
    
    // Error categories
    const errorTypes = {};
    
    for (let batch = 0; batch < totalBatches; batch++) {
        const batchPromises = [];
        const batchStart = Date.now();
        
        // Generate and execute batch
        for (let i = 0; i < batchSize; i++) {
            const query = generateRandomQuery();
            batchPromises.push(executeQuery(server, query));
        }
        
        // Wait for batch to complete
        const results = await Promise.all(batchPromises);
        const batchDuration = Date.now() - batchStart;
        totalDuration += batchDuration;
        
        // Process results
        results.forEach(result => {
            if (result.success) {
                successCount++;
            } else {
                errorCount++;
                const errorKey = result.error?.substring(0, 50) || 'Unknown';
                errorTypes[errorKey] = (errorTypes[errorKey] || 0) + 1;
            }
        });
        
        // Progress update every 100 batches
        if (batch % 100 === 0 && batch > 0) {
            const elapsed = (Date.now() - startTime) / 1000;
            const queriesCompleted = batch * batchSize;
            const qps = queriesCompleted / elapsed;
            const eta = (totalQueries - queriesCompleted) / qps;
            
            console.log(`Progress: ${queriesCompleted.toLocaleString()}/${totalQueries.toLocaleString()} queries`);
            console.log(`  Success rate: ${((successCount / queriesCompleted) * 100).toFixed(2)}%`);
            console.log(`  Queries/sec: ${qps.toFixed(2)}`);
            console.log(`  ETA: ${eta.toFixed(0)} seconds`);
            console.log('');
        }
    }
    
    const totalTime = Date.now() - startTime;
    
    // Final statistics
    console.log('\n' + '='.repeat(70));
    console.log(`FINAL RESULTS - ${server.name}`);
    console.log('='.repeat(70));
    console.log(`Total queries: ${totalQueries.toLocaleString()}`);
    console.log(`Successful: ${successCount.toLocaleString()} (${((successCount / totalQueries) * 100).toFixed(2)}%)`);
    console.log(`Failed: ${errorCount.toLocaleString()} (${((errorCount / totalQueries) * 100).toFixed(2)}%)`);
    console.log(`Total time: ${(totalTime / 1000).toFixed(2)} seconds`);
    console.log(`Average QPS: ${(totalQueries / (totalTime / 1000)).toFixed(2)}`);
    console.log(`Average latency: ${(totalDuration / totalBatches).toFixed(2)}ms per batch of ${batchSize}`);
    
    if (Object.keys(errorTypes).length > 0) {
        console.log('\nTop error types:');
        Object.entries(errorTypes)
            .sort((a, b) => b[1] - a[1])
            .slice(0, 5)
            .forEach(([error, count]) => {
                console.log(`  ${error}... : ${count}`);
            });
    }
    
    return {
        server: server.name,
        totalQueries,
        successCount,
        errorCount,
        totalTime,
        qps: totalQueries / (totalTime / 1000)
    };
}

// Compare both frameworks
async function compareFrameworks() {
    console.log('MILLION QUERY PERFORMANCE COMPARISON');
    console.log('Testing both Spring Boot and Quarkus with 1 million queries each\n');
    
    // Test Spring Boot
    const springBootResults = await runMillionTest(SERVERS.springBoot);
    
    // Test Quarkus
    const quarkusResults = await runMillionTest(SERVERS.quarkus);
    
    // Comparison summary
    console.log('\n' + '='.repeat(70));
    console.log('PERFORMANCE COMPARISON SUMMARY');
    console.log('='.repeat(70));
    console.log('\nFramework Comparison:');
    console.log('-'.repeat(40));
    
    const springBootQPS = springBootResults.qps;
    const quarkusQPS = quarkusResults.qps;
    const speedDiff = ((Math.max(springBootQPS, quarkusQPS) / Math.min(springBootQPS, quarkusQPS) - 1) * 100).toFixed(1);
    const faster = springBootQPS > quarkusQPS ? 'Spring Boot' : 'Quarkus';
    
    console.log(`Spring Boot: ${springBootQPS.toFixed(2)} QPS`);
    console.log(`Quarkus: ${quarkusQPS.toFixed(2)} QPS`);
    console.log(`\n${faster} is ${speedDiff}% faster`);
    
    console.log('\nSuccess Rates:');
    console.log(`Spring Boot: ${((springBootResults.successCount / springBootResults.totalQueries) * 100).toFixed(2)}%`);
    console.log(`Quarkus: ${((quarkusResults.successCount / quarkusResults.totalQueries) * 100).toFixed(2)}%`);
    
    console.log('\nTotal Time:');
    console.log(`Spring Boot: ${(springBootResults.totalTime / 1000).toFixed(2)} seconds`);
    console.log(`Quarkus: ${(quarkusResults.totalTime / 1000).toFixed(2)} seconds`);
}

// Check if running specific server test or comparison
const args = process.argv.slice(2);
if (args[0] === 'springboot') {
    runMillionTest(SERVERS.springBoot).catch(console.error);
} else if (args[0] === 'quarkus') {
    runMillionTest(SERVERS.quarkus).catch(console.error);
} else {
    // Run comparison by default
    compareFrameworks().catch(console.error);
}