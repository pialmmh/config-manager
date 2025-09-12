import { StellarClient, QueryBuilder } from './stellar-client.js';
import chalk from 'chalk';
import fs from 'fs/promises';
import path from 'path';

const client = new StellarClient();
const logFile = 'test-results.log';

/**
 * Logger utility
 */
class TestLogger {
    constructor() {
        this.results = [];
        this.startTime = Date.now();
    }
    
    async init() {
        // Clear previous log file
        await fs.writeFile(logFile, `Test Run Started: ${new Date().toISOString()}\n${'='.repeat(80)}\n\n`);
    }
    
    async log(message, type = 'info') {
        const timestamp = new Date().toISOString();
        const logEntry = `[${timestamp}] [${type.toUpperCase()}] ${message}\n`;
        
        // Write to file
        await fs.appendFile(logFile, logEntry);
        
        // Console output with colors
        switch(type) {
            case 'success':
                console.log(chalk.green(`✓ ${message}`));
                break;
            case 'error':
                console.log(chalk.red(`✗ ${message}`));
                break;
            case 'warning':
                console.log(chalk.yellow(`⚠ ${message}`));
                break;
            case 'info':
                console.log(chalk.blue(`ℹ ${message}`));
                break;
            default:
                console.log(message);
        }
    }
    
    async logTestResult(testName, success, details = {}) {
        const result = {
            test: testName,
            success,
            timestamp: new Date().toISOString(),
            details
        };
        
        this.results.push(result);
        
        const status = success ? 'PASSED' : 'FAILED';
        const color = success ? chalk.green : chalk.red;
        
        console.log(color(`\n${status}: ${testName}`));
        
        await fs.appendFile(logFile, `\nTest: ${testName}\nStatus: ${status}\nDetails: ${JSON.stringify(details, null, 2)}\n${'─'.repeat(40)}\n`);
    }
    
    async writeSummary() {
        const duration = Date.now() - this.startTime;
        const passed = this.results.filter(r => r.success).length;
        const failed = this.results.filter(r => !r.success).length;
        
        const summary = `
${'='.repeat(80)}
TEST SUMMARY
${'='.repeat(80)}
Total Tests: ${this.results.length}
Passed: ${passed}
Failed: ${failed}
Duration: ${duration}ms
Success Rate: ${(passed / this.results.length * 100).toFixed(2)}%
${'='.repeat(80)}`;
        
        console.log(chalk.cyan(summary));
        await fs.appendFile(logFile, summary);
    }
}

/**
 * Test Suite
 */
class StellarTestSuite {
    constructor() {
        this.logger = new TestLogger();
        this.tests = [];
    }
    
    /**
     * Test 1: Health Check
     */
    async testHealthCheck() {
        const testName = 'Health Check';
        try {
            const health = await client.health();
            const success = health.status === 'healthy';
            await this.logger.logTestResult(testName, success, health);
            return success;
        } catch (error) {
            await this.logger.logTestResult(testName, false, { error: error.message });
            return false;
        }
    }
    
    /**
     * Test 2: Simple Person Query
     */
    async testSimplePersonQuery() {
        const testName = 'Simple Person Query';
        try {
            const query = QueryBuilder.simple('person', { name: 'Ali' }, QueryBuilder.page(10));
            
            await this.logger.log(`Sending query: ${JSON.stringify(query)}`, 'info');
            
            const result = await client.query(query);
            const success = result.success === true && Array.isArray(result.data);
            
            await this.logger.logTestResult(testName, success, {
                query,
                resultCount: result.data?.length || 0,
                sample: result.data?.slice(0, 2)
            });
            
            return success;
        } catch (error) {
            await this.logger.logTestResult(testName, false, { error: error.message });
            return false;
        }
    }
    
    /**
     * Test 3: Person with Orders Query
     */
    async testPersonWithOrders() {
        const testName = 'Person with Orders Query';
        try {
            const query = QueryBuilder.withIncludes(
                'person',
                {}, // No criteria - get all persons
                [
                    QueryBuilder.simple('order', {}, QueryBuilder.page(5))
                ],
                QueryBuilder.page(10)
            );
            
            await this.logger.log(`Sending nested query: ${JSON.stringify(query)}`, 'info');
            
            const result = await client.query(query);
            const success = result.success === true && Array.isArray(result.data);
            
            // Check if we have order data in results
            const hasOrderData = result.data?.some(row => 
                Object.keys(row).some(key => key.startsWith('o__'))
            );
            
            await this.logger.logTestResult(testName, success, {
                query,
                resultCount: result.data?.length || 0,
                hasOrderData,
                sampleKeys: result.data?.[0] ? Object.keys(result.data[0]) : []
            });
            
            return success;
        } catch (error) {
            await this.logger.logTestResult(testName, false, { error: error.message });
            return false;
        }
    }
    
    /**
     * Test 4: Deep Nested Query (Person -> Order -> Invoice)
     */
    async testDeepNestedQuery() {
        const testName = 'Deep Nested Query (Person->Order->Invoice)';
        try {
            const query = {
                kind: 'person',
                criteria: {},
                page: { limit: 5, offset: 0 },
                include: [
                    {
                        kind: 'order',
                        page: { limit: 3, offset: 0 },
                        include: [
                            {
                                kind: 'invoice',
                                criteria: { status: 'paid' },
                                page: { limit: 2, offset: 0 }
                            }
                        ]
                    }
                ]
            };
            
            await this.logger.log(`Sending deep nested query: ${JSON.stringify(query)}`, 'info');
            
            const result = await client.query(query);
            const success = result.success === true && Array.isArray(result.data);
            
            // Check for all levels of data
            const hasPersonData = result.data?.some(row => 
                Object.keys(row).some(key => key.startsWith('p__'))
            );
            const hasOrderData = result.data?.some(row => 
                Object.keys(row).some(key => key.startsWith('o__'))
            );
            const hasInvoiceData = result.data?.some(row => 
                Object.keys(row).some(key => key.startsWith('i__'))
            );
            
            await this.logger.logTestResult(testName, success, {
                query,
                resultCount: result.data?.length || 0,
                hasPersonData,
                hasOrderData,
                hasInvoiceData,
                sampleRow: result.data?.[0]
            });
            
            return success;
        } catch (error) {
            await this.logger.logTestResult(testName, false, { error: error.message });
            return false;
        }
    }
    
    /**
     * Test 5: Query with Pagination
     */
    async testPagination() {
        const testName = 'Pagination Test';
        try {
            // First page
            const page1Query = QueryBuilder.simple('person', {}, QueryBuilder.page(5, 0));
            const page1Result = await client.query(page1Query);
            
            // Second page
            const page2Query = QueryBuilder.simple('person', {}, QueryBuilder.page(5, 5));
            const page2Result = await client.query(page2Query);
            
            const success = 
                page1Result.success === true && 
                page2Result.success === true &&
                Array.isArray(page1Result.data) &&
                Array.isArray(page2Result.data);
            
            // Check if results are different (pagination working)
            const page1Ids = page1Result.data?.map(row => row['p__id']) || [];
            const page2Ids = page2Result.data?.map(row => row['p__id']) || [];
            const hasUniqueResults = !page1Ids.some(id => page2Ids.includes(id));
            
            await this.logger.logTestResult(testName, success && hasUniqueResults, {
                page1Count: page1Result.data?.length || 0,
                page2Count: page2Result.data?.length || 0,
                hasUniqueResults,
                page1Ids,
                page2Ids
            });
            
            return success && hasUniqueResults;
        } catch (error) {
            await this.logger.logTestResult(testName, false, { error: error.message });
            return false;
        }
    }
    
    /**
     * Test 6: Error Handling - Invalid Kind
     */
    async testInvalidKind() {
        const testName = 'Error Handling - Invalid Kind';
        try {
            const query = QueryBuilder.simple('invalid_kind', {});
            const result = await client.query(query);
            
            // This should fail, so if we get here, the test failed
            await this.logger.logTestResult(testName, false, { 
                error: 'Expected error but got success',
                result 
            });
            return false;
        } catch (error) {
            // Expected to catch an error
            const success = error.message.includes('Unknown kind') || 
                          error.message.includes('API Error');
            await this.logger.logTestResult(testName, success, { 
                expectedError: true,
                errorMessage: error.message 
            });
            return success;
        }
    }
    
    /**
     * Run all tests
     */
    async runAll() {
        await this.logger.init();
        await this.logger.log('Starting Stellar Integration Tests', 'info');
        
        console.log(chalk.cyan('\n' + '='.repeat(80)));
        console.log(chalk.cyan.bold('STELLAR INTEGRATION TEST SUITE'));
        console.log(chalk.cyan('='.repeat(80) + '\n'));
        
        // Check if server is running
        const health = await client.health();
        if (health.status !== 'healthy') {
            await this.logger.log('Server is not healthy! Make sure Quarkus is running on port 8080', 'error');
            console.log(chalk.red('\n⚠️  Please start the Quarkus server first:'));
            console.log(chalk.yellow('cd ../stellar-rest && mvn quarkus:dev'));
            console.log(chalk.yellow('Server should be running on port 8090'));
            return;
        }
        
        // Run tests
        const tests = [
            () => this.testHealthCheck(),
            () => this.testSimplePersonQuery(),
            () => this.testPersonWithOrders(),
            () => this.testDeepNestedQuery(),
            () => this.testPagination(),
            () => this.testInvalidKind()
        ];
        
        for (const test of tests) {
            await test();
            // Small delay between tests
            await new Promise(resolve => setTimeout(resolve, 500));
        }
        
        // Write summary
        await this.logger.writeSummary();
        
        console.log(chalk.gray(`\nTest results written to: ${path.resolve(logFile)}`));
    }
}

// Run tests
const suite = new StellarTestSuite();
suite.runAll().catch(error => {
    console.error(chalk.red('Fatal error:'), error);
    process.exit(1);
});