import axios from 'axios';

const SPRING_BOOT_PORT = 8091;
const QUARKUS_PORT = 8090;

// Test configuration
const TEST_CONFIG = {
    springBoot: {
        baseUrl: `http://localhost:${SPRING_BOOT_PORT}/api`,
        name: 'Spring Boot'
    },
    quarkus: {
        baseUrl: `http://localhost:${QUARKUS_PORT}/api`,
        name: 'Quarkus'
    }
};

// Test data for modification operations
const modificationTests = [
    {
        name: 'Insert Category with Product',
        payload: {
            entityName: 'category',
            operation: 'INSERT',
            data: {
                CategoryName: 'Test Electronics',
                Description: 'Electronic products test'
            },
            include: [
                {
                    entityName: 'product',
                    operation: 'INSERT',
                    data: {
                        ProductName: 'Test Laptop',
                        SupplierID: 1,
                        UnitsInStock: 50,
                        UnitPrice: 999.99
                    }
                }
            ]
        }
    },
    {
        name: 'Update Product',
        payload: {
            entityName: 'product',
            operation: 'UPDATE',
            data: {
                UnitsInStock: 75,
                UnitPrice: 899.99
            },
            criteria: {
                ProductName: 'Test Laptop'
            }
        }
    },
    {
        name: 'Delete Product',
        payload: {
            entityName: 'product',
            operation: 'DELETE',
            criteria: {
                ProductName: 'Test Laptop'
            }
        }
    }
];

// Query tests
const queryTests = [
    {
        name: 'Simple category query',
        payload: {
            kind: 'category',
            limit: 5
        }
    },
    {
        name: 'Category with products',
        payload: {
            kind: 'category',
            limit: 3,
            include: [
                {
                    kind: 'product',
                    limit: 5
                }
            ]
        }
    },
    {
        name: 'Customer with orders and details',
        payload: {
            kind: 'customer',
            limit: 2,
            include: [
                {
                    kind: 'salesorder',
                    limit: 3,
                    include: [
                        {
                            kind: 'orderdetail',
                            limit: 5
                        }
                    ]
                }
            ]
        }
    }
];

async function testEndpoint(config, endpoint, payload, testName) {
    const startTime = Date.now();
    try {
        const response = await axios.post(`${config.baseUrl}${endpoint}`, payload, {
            headers: { 'Content-Type': 'application/json' },
            timeout: 10000
        });
        const duration = Date.now() - startTime;
        
        return {
            success: true,
            duration,
            dataCount: response.data.data ? response.data.data.length : 0,
            cacheStats: response.data.cacheStats
        };
    } catch (error) {
        const duration = Date.now() - startTime;
        return {
            success: false,
            duration,
            error: error.response?.data?.error || error.message
        };
    }
}

async function runTestSuite(config) {
    console.log(`\n${'='.repeat(60)}`);
    console.log(`Testing ${config.name} on port ${config.baseUrl.split(':')[2].split('/')[0]}`);
    console.log(`${'='.repeat(60)}\n`);

    // Test query endpoint
    console.log('Testing Query Endpoint:');
    console.log('-'.repeat(40));
    for (const test of queryTests) {
        const result = await testEndpoint(config, '/query', test.payload, test.name);
        console.log(`✓ ${test.name}:`);
        console.log(`  Duration: ${result.duration}ms`);
        if (result.success) {
            console.log(`  Records: ${result.dataCount}`);
        } else {
            console.log(`  Error: ${result.error}`);
        }
    }

    // Test modification endpoint
    console.log('\nTesting Modification Endpoint:');
    console.log('-'.repeat(40));
    for (const test of modificationTests) {
        const result = await testEndpoint(config, '/modify', test.payload, test.name);
        console.log(`✓ ${test.name}:`);
        console.log(`  Duration: ${result.duration}ms`);
        if (result.success) {
            if (result.cacheStats) {
                console.log(`  Cache hits: ${result.cacheStats.usageCount || 0}`);
            }
        } else {
            console.log(`  Error: ${result.error}`);
        }
    }

    // Performance test with multiple queries
    console.log('\nPerformance Test (100 queries):');
    console.log('-'.repeat(40));
    const perfStart = Date.now();
    let successCount = 0;
    let totalDuration = 0;

    for (let i = 0; i < 100; i++) {
        const test = queryTests[i % queryTests.length];
        const result = await testEndpoint(config, '/query', test.payload, `Query ${i}`);
        if (result.success) {
            successCount++;
            totalDuration += result.duration;
        }
    }

    const perfDuration = Date.now() - perfStart;
    console.log(`Total time: ${perfDuration}ms`);
    console.log(`Success rate: ${successCount}/100 (${successCount}%)`);
    console.log(`Average query time: ${(totalDuration / successCount).toFixed(2)}ms`);
    console.log(`Queries per second: ${(100000 / perfDuration).toFixed(2)}`);
}

async function compareFrameworks() {
    console.log('\n' + '='.repeat(60));
    console.log('FRAMEWORK COMPARISON TEST');
    console.log('='.repeat(60));

    // Test Spring Boot
    await runTestSuite(TEST_CONFIG.springBoot);
    
    // Test Quarkus
    await runTestSuite(TEST_CONFIG.quarkus);

    // Comparison summary
    console.log('\n' + '='.repeat(60));
    console.log('COMPARISON SUMMARY');
    console.log('='.repeat(60));
    console.log('Both frameworks tested with identical queries and modifications');
    console.log('Check the performance metrics above for comparison');
}

// Run the comparison
compareFrameworks().catch(console.error);