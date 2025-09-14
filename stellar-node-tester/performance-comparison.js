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

// Test queries with varying complexity
const testQueries = [
    // Simple queries
    { name: 'Simple category', query: { kind: 'category', limit: 10 }},
    { name: 'Simple product', query: { kind: 'product', limit: 20 }},
    { name: 'Simple customer', query: { kind: 'customer', limit: 15 }},
    
    // Two-level queries
    { 
        name: 'Category with products',
        query: {
            kind: 'category',
            limit: 5,
            include: [{ kind: 'product', limit: 10 }]
        }
    },
    { 
        name: 'Customer with orders',
        query: {
            kind: 'customer',
            limit: 10,
            include: [{ kind: 'salesorder', limit: 5 }]
        }
    },
    
    // Three-level queries
    {
        name: 'Customer->Order->Details',
        query: {
            kind: 'customer',
            limit: 5,
            include: [{
                kind: 'salesorder',
                limit: 3,
                include: [{ kind: 'orderdetail', limit: 5 }]
            }]
        }
    }
];

async function runPerformanceTest(server, numQueries = 10000) {
    console.log(`\n${'='.repeat(60)}`);
    console.log(`Testing ${server.name} (Port ${server.port})`);
    console.log(`Running ${numQueries.toLocaleString()} queries...`);
    console.log(`${'='.repeat(60)}\n`);
    
    const results = {
        totalQueries: numQueries,
        successCount: 0,
        errorCount: 0,
        totalDuration: 0,
        queryTypes: {}
    };
    
    const startTime = Date.now();
    const batchSize = 100;
    const numBatches = Math.ceil(numQueries / batchSize);
    
    for (let batch = 0; batch < numBatches; batch++) {
        const batchPromises = [];
        const currentBatchSize = Math.min(batchSize, numQueries - batch * batchSize);
        
        for (let i = 0; i < currentBatchSize; i++) {
            const testQuery = testQueries[Math.floor(Math.random() * testQueries.length)];
            const queryName = testQuery.name;
            
            if (!results.queryTypes[queryName]) {
                results.queryTypes[queryName] = { count: 0, success: 0, totalTime: 0 };
            }
            results.queryTypes[queryName].count++;
            
            const queryStart = Date.now();
            batchPromises.push(
                axios.post(`${server.baseUrl}/query`, testQuery.query, {
                    headers: { 'Content-Type': 'application/json' },
                    timeout: 5000
                })
                .then(response => {
                    const duration = Date.now() - queryStart;
                    results.queryTypes[queryName].success++;
                    results.queryTypes[queryName].totalTime += duration;
                    results.successCount++;
                    results.totalDuration += duration;
                    return { success: true };
                })
                .catch(error => {
                    results.errorCount++;
                    return { success: false, error: error.message };
                })
            );
        }
        
        await Promise.all(batchPromises);
        
        // Progress update every 10 batches
        if (batch % 10 === 0 && batch > 0) {
            const completed = batch * batchSize;
            const elapsed = (Date.now() - startTime) / 1000;
            const qps = completed / elapsed;
            console.log(`Progress: ${completed}/${numQueries} queries, ${qps.toFixed(2)} QPS`);
        }
    }
    
    const totalTime = Date.now() - startTime;
    results.totalTime = totalTime;
    results.qps = numQueries / (totalTime / 1000);
    
    // Print results
    console.log(`\nResults for ${server.name}:`);
    console.log('-'.repeat(40));
    console.log(`Total queries: ${results.totalQueries.toLocaleString()}`);
    console.log(`Successful: ${results.successCount.toLocaleString()} (${((results.successCount / results.totalQueries) * 100).toFixed(2)}%)`);
    console.log(`Failed: ${results.errorCount.toLocaleString()}`);
    console.log(`Total time: ${(results.totalTime / 1000).toFixed(2)} seconds`);
    console.log(`Queries per second: ${results.qps.toFixed(2)}`);
    console.log(`Average latency: ${(results.totalDuration / results.successCount).toFixed(2)}ms`);
    
    console.log('\nQuery type breakdown:');
    Object.entries(results.queryTypes).forEach(([name, stats]) => {
        if (stats.success > 0) {
            console.log(`  ${name}: ${stats.success}/${stats.count}, avg ${(stats.totalTime / stats.success).toFixed(2)}ms`);
        }
    });
    
    return results;
}

async function compareFrameworks() {
    console.log('\n' + '='.repeat(60));
    console.log('PERFORMANCE COMPARISON: Spring Boot vs Quarkus');
    console.log('='.repeat(60));
    
    const numQueries = 10000; // 10K queries for faster test
    
    // Test Spring Boot
    const springBootResults = await runPerformanceTest(SERVERS.springBoot, numQueries);
    
    // Test Quarkus
    const quarkusResults = await runPerformanceTest(SERVERS.quarkus, numQueries);
    
    // Comparison summary
    console.log('\n' + '='.repeat(60));
    console.log('COMPARISON SUMMARY');
    console.log('='.repeat(60));
    
    const springQPS = springBootResults.qps;
    const quarkusQPS = quarkusResults.qps;
    const springAvgLatency = springBootResults.totalDuration / springBootResults.successCount;
    const quarkusAvgLatency = quarkusResults.totalDuration / quarkusResults.successCount;
    
    console.log('\nThroughput (Queries Per Second):');
    console.log(`  Spring Boot: ${springQPS.toFixed(2)} QPS`);
    console.log(`  Quarkus: ${quarkusQPS.toFixed(2)} QPS`);
    if (springQPS > quarkusQPS) {
        console.log(`  → Spring Boot is ${((springQPS / quarkusQPS - 1) * 100).toFixed(1)}% faster`);
    } else {
        console.log(`  → Quarkus is ${((quarkusQPS / springQPS - 1) * 100).toFixed(1)}% faster`);
    }
    
    console.log('\nLatency (Average Response Time):');
    console.log(`  Spring Boot: ${springAvgLatency.toFixed(2)}ms`);
    console.log(`  Quarkus: ${quarkusAvgLatency.toFixed(2)}ms`);
    if (springAvgLatency < quarkusAvgLatency) {
        console.log(`  → Spring Boot is ${((quarkusAvgLatency / springAvgLatency - 1) * 100).toFixed(1)}% faster`);
    } else {
        console.log(`  → Quarkus is ${((springAvgLatency / quarkusAvgLatency - 1) * 100).toFixed(1)}% faster`);
    }
    
    console.log('\nSuccess Rate:');
    console.log(`  Spring Boot: ${((springBootResults.successCount / springBootResults.totalQueries) * 100).toFixed(2)}%`);
    console.log(`  Quarkus: ${((quarkusResults.successCount / quarkusResults.totalQueries) * 100).toFixed(2)}%`);
    
    console.log('\nTotal Processing Time:');
    console.log(`  Spring Boot: ${(springBootResults.totalTime / 1000).toFixed(2)} seconds`);
    console.log(`  Quarkus: ${(quarkusResults.totalTime / 1000).toFixed(2)} seconds`);
    
    console.log('\n' + '='.repeat(60));
    console.log('Test completed successfully!');
}

// Run the comparison
compareFrameworks().catch(console.error);