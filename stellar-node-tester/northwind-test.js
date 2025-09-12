import { StellarClient } from './stellar-client.js';
import chalk from 'chalk';
import fs from 'fs/promises';

const client = new StellarClient();
const logFile = 'northwind-test-results.log';

/**
 * Test Logger
 */
class TestLogger {
    constructor() {
        this.successCount = 0;
        this.failureCount = 0;
        this.startTime = Date.now();
        this.failedQueries = [];
    }
    
    async init() {
        await fs.writeFile(logFile, `Northwind Test Run Started: ${new Date().toISOString()}\n${'='.repeat(80)}\n\n`);
    }
    
    async logResult(queryNum, query, success, result, error = null) {
        if (success) {
            this.successCount++;
            console.log(chalk.green(`✓ Query ${queryNum}: ${query.kind} - ${result.count || 0} rows`));
        } else {
            this.failureCount++;
            this.failedQueries.push({ queryNum, query, error });
            console.log(chalk.red(`✗ Query ${queryNum}: ${query.kind} - ${error?.message || 'Failed'}`));
            
            // Log full error details to file
            const errorDetails = `
Query ${queryNum} FAILED:
Query: ${JSON.stringify(query, null, 2)}
Error: ${error?.message || 'Unknown error'}
Stack: ${error?.stackTrace || error?.stack || 'N/A'}
${'─'.repeat(40)}
`;
            await fs.appendFile(logFile, errorDetails);
        }
    }
    
    async writeSummary() {
        const duration = Date.now() - this.startTime;
        const total = this.successCount + this.failureCount;
        const successRate = (this.successCount / total * 100).toFixed(2);
        
        const summary = `
${'='.repeat(80)}
TEST SUMMARY
${'='.repeat(80)}
Total Queries: ${total}
Successful: ${this.successCount}
Failed: ${this.failureCount}
Success Rate: ${successRate}%
Duration: ${duration}ms
${'='.repeat(80)}

Failed Queries:
${this.failedQueries.map(f => `  - Query ${f.queryNum}: ${f.query.kind} - ${f.error?.message}`).join('\n')}
`;
        
        console.log(chalk.cyan(summary));
        await fs.appendFile(logFile, summary);
    }
}

/**
 * Query Generator - Creates diverse queries for testing
 */
class QueryGenerator {
    constructor() {
        this.queries = [];
    }
    
    /**
     * Generate all test queries
     */
    generateAll() {
        // 1. Simple queries without pagination (50 queries)
        this.generateSimpleQueries();
        
        // 2. Simple queries with pagination (100 queries)
        this.generatePaginatedQueries();
        
        // 3. Two-level queries (200 queries)
        this.generateTwoLevelQueries();
        
        // 4. Three-level queries (200 queries)
        this.generateThreeLevelQueries();
        
        // 5. Four-level queries (150 queries)
        this.generateFourLevelQueries();
        
        // 6. Complex criteria queries (100 queries)
        this.generateComplexCriteriaQueries();
        
        // 7. Mixed pagination queries (100 queries)
        this.generateMixedPaginationQueries();
        
        // 8. Edge case queries (100 queries)
        this.generateEdgeCaseQueries();
        
        return this.queries;
    }
    
    /**
     * 1. Simple queries without pagination
     */
    generateSimpleQueries() {
        const entities = ['category', 'product', 'customer', 'salesorder', 'orderdetail', 'employee', 'shipper', 'supplier'];
        
        // Basic queries for each entity
        entities.forEach(entity => {
            // No criteria, no pagination
            this.queries.push({ kind: entity });
            
            // With different criteria
            switch(entity) {
                case 'category':
                    this.queries.push({ kind: entity, criteria: { categoryId: 1 } });
                    this.queries.push({ kind: entity, criteria: { categoryName: 'Beverages' } });
                    break;
                case 'product':
                    this.queries.push({ kind: entity, criteria: { productId: 1 } });
                    this.queries.push({ kind: entity, criteria: { categoryId: 2 } });
                    this.queries.push({ kind: entity, criteria: { supplierId: 1 } });
                    this.queries.push({ kind: entity, criteria: { discontinued: '0' } });
                    break;
                case 'customer':
                    this.queries.push({ kind: entity, criteria: { custId: 1 } });
                    this.queries.push({ kind: entity, criteria: { country: 'USA' } });
                    this.queries.push({ kind: entity, criteria: { city: 'London' } });
                    break;
                case 'salesorder':
                    this.queries.push({ kind: entity, criteria: { orderId: 10248 } });
                    this.queries.push({ kind: entity, criteria: { custId: 1 } });
                    this.queries.push({ kind: entity, criteria: { employeeId: 1 } });
                    break;
                case 'orderdetail':
                    this.queries.push({ kind: entity, criteria: { orderId: 10248 } });
                    this.queries.push({ kind: entity, criteria: { productId: 1 } });
                    break;
                case 'employee':
                    this.queries.push({ kind: entity, criteria: { employeeId: 1 } });
                    this.queries.push({ kind: entity, criteria: { city: 'Seattle' } });
                    break;
                case 'supplier':
                    this.queries.push({ kind: entity, criteria: { supplierId: 1 } });
                    this.queries.push({ kind: entity, criteria: { country: 'UK' } });
                    break;
                case 'shipper':
                    this.queries.push({ kind: entity, criteria: { shipperId: 1 } });
                    break;
            }
        });
    }
    
    /**
     * 2. Simple queries with pagination
     */
    generatePaginatedQueries() {
        const entities = ['category', 'product', 'customer', 'salesorder', 'orderdetail'];
        const limits = [1, 5, 10, 20, 50, 100];
        const offsets = [0, 5, 10, 20, 50];
        
        entities.forEach(entity => {
            limits.forEach(limit => {
                offsets.forEach(offset => {
                    this.queries.push({
                        kind: entity,
                        page: { limit, offset }
                    });
                });
            });
        });
    }
    
    /**
     * 3. Two-level queries
     */
    generateTwoLevelQueries() {
        // Category -> Product
        for (let i = 0; i < 20; i++) {
            this.queries.push({
                kind: 'category',
                page: { limit: Math.floor(Math.random() * 10) + 1, offset: 0 },
                include: [{
                    kind: 'product',
                    page: { limit: Math.floor(Math.random() * 20) + 1, offset: 0 }
                }]
            });
        }
        
        // Customer -> SalesOrder
        for (let i = 0; i < 30; i++) {
            this.queries.push({
                kind: 'customer',
                criteria: { country: ['USA', 'UK', 'Germany', 'France', 'Spain'][i % 5] },
                page: { limit: Math.floor(Math.random() * 15) + 1, offset: i * 2 },
                include: [{
                    kind: 'salesorder',
                    page: { limit: Math.floor(Math.random() * 10) + 1, offset: 0 }
                }]
            });
        }
        
        // SalesOrder -> OrderDetail
        for (let i = 0; i < 30; i++) {
            this.queries.push({
                kind: 'salesorder',
                page: { limit: Math.floor(Math.random() * 20) + 1, offset: i },
                include: [{
                    kind: 'orderdetail',
                    page: { limit: Math.floor(Math.random() * 15) + 1, offset: 0 }
                }]
            });
        }
        
        // Employee -> SalesOrder
        for (let i = 0; i < 20; i++) {
            this.queries.push({
                kind: 'employee',
                page: { limit: 5, offset: 0 },
                include: [{
                    kind: 'salesorder',
                    page: { limit: Math.floor(Math.random() * 30) + 1, offset: 0 }
                }]
            });
        }
        
        // Supplier -> Product
        for (let i = 0; i < 20; i++) {
            this.queries.push({
                kind: 'supplier',
                page: { limit: 10, offset: 0 },
                include: [{
                    kind: 'product',
                    page: { limit: Math.floor(Math.random() * 25) + 1, offset: 0 }
                }]
            });
        }
        
        // Product -> OrderDetail
        for (let i = 0; i < 20; i++) {
            this.queries.push({
                kind: 'product',
                criteria: { categoryId: (i % 8) + 1 },
                page: { limit: 15, offset: 0 },
                include: [{
                    kind: 'orderdetail',
                    page: { limit: Math.floor(Math.random() * 20) + 1, offset: 0 }
                }]
            });
        }
        
        // OrderDetail -> Product
        for (let i = 0; i < 20; i++) {
            this.queries.push({
                kind: 'orderdetail',
                page: { limit: 50, offset: i * 10 },
                include: [{
                    kind: 'product'
                }]
            });
        }
        
        // Shipper -> SalesOrder
        for (let i = 0; i < 20; i++) {
            this.queries.push({
                kind: 'shipper',
                include: [{
                    kind: 'salesorder',
                    page: { limit: Math.floor(Math.random() * 100) + 1, offset: 0 }
                }]
            });
        }
    }
    
    /**
     * 4. Three-level queries
     */
    generateThreeLevelQueries() {
        // Category -> Product -> OrderDetail
        for (let i = 0; i < 25; i++) {
            this.queries.push({
                kind: 'category',
                page: { limit: 5, offset: 0 },
                include: [{
                    kind: 'product',
                    page: { limit: 10, offset: 0 },
                    include: [{
                        kind: 'orderdetail',
                        page: { limit: 5, offset: 0 }
                    }]
                }]
            });
        }
        
        // Customer -> SalesOrder -> OrderDetail
        for (let i = 0; i < 40; i++) {
            const customerLimit = Math.floor(Math.random() * 10) + 1;
            const orderLimit = Math.floor(Math.random() * 10) + 1;
            const detailLimit = Math.floor(Math.random() * 5) + 1;
            
            this.queries.push({
                kind: 'customer',
                page: { limit: customerLimit, offset: i },
                include: [{
                    kind: 'salesorder',
                    page: { limit: orderLimit, offset: 0 },
                    include: [{
                        kind: 'orderdetail',
                        page: { limit: detailLimit, offset: 0 }
                    }]
                }]
            });
        }
        
        // Supplier -> Product -> OrderDetail
        for (let i = 0; i < 25; i++) {
            this.queries.push({
                kind: 'supplier',
                page: { limit: 5, offset: i },
                include: [{
                    kind: 'product',
                    page: { limit: 8, offset: 0 },
                    include: [{
                        kind: 'orderdetail',
                        page: { limit: 3, offset: 0 }
                    }]
                }]
            });
        }
        
        // Employee -> SalesOrder -> OrderDetail
        for (let i = 0; i < 20; i++) {
            this.queries.push({
                kind: 'employee',
                include: [{
                    kind: 'salesorder',
                    page: { limit: 20, offset: i * 5 },
                    include: [{
                        kind: 'orderdetail',
                        page: { limit: 10, offset: 0 }
                    }]
                }]
            });
        }
        
        // SalesOrder -> OrderDetail -> Product
        for (let i = 0; i < 30; i++) {
            this.queries.push({
                kind: 'salesorder',
                page: { limit: 10, offset: i * 10 },
                include: [{
                    kind: 'orderdetail',
                    page: { limit: 5, offset: 0 },
                    include: [{
                        kind: 'product'
                    }]
                }]
            });
        }
        
        // Mixed pagination patterns
        for (let i = 0; i < 30; i++) {
            const pattern = i % 3;
            if (pattern === 0) {
                // No limit at first level
                this.queries.push({
                    kind: 'category',
                    include: [{
                        kind: 'product',
                        page: { limit: 10, offset: 0 },
                        include: [{
                            kind: 'orderdetail',
                            page: { limit: 5, offset: 0 }
                        }]
                    }]
                });
            } else if (pattern === 1) {
                // No limit at second level
                this.queries.push({
                    kind: 'customer',
                    page: { limit: 5, offset: 0 },
                    include: [{
                        kind: 'salesorder',
                        include: [{
                            kind: 'orderdetail',
                            page: { limit: 10, offset: 0 }
                        }]
                    }]
                });
            } else {
                // No limit at third level
                this.queries.push({
                    kind: 'supplier',
                    page: { limit: 3, offset: 0 },
                    include: [{
                        kind: 'product',
                        page: { limit: 5, offset: 0 },
                        include: [{
                            kind: 'orderdetail'
                        }]
                    }]
                });
            }
        }
        
        // Shipper -> SalesOrder -> OrderDetail
        for (let i = 0; i < 10; i++) {
            this.queries.push({
                kind: 'shipper',
                include: [{
                    kind: 'salesorder',
                    page: { limit: 50, offset: i * 20 },
                    include: [{
                        kind: 'orderdetail',
                        page: { limit: 10, offset: 0 }
                    }]
                }]
            });
        }
    }
    
    /**
     * 5. Four-level queries
     */
    generateFourLevelQueries() {
        // Category -> Product -> OrderDetail -> SalesOrder (via orderId)
        for (let i = 0; i < 20; i++) {
            this.queries.push({
                kind: 'category',
                page: { limit: 3, offset: 0 },
                include: [{
                    kind: 'product',
                    page: { limit: 5, offset: 0 },
                    include: [{
                        kind: 'orderdetail',
                        page: { limit: 3, offset: 0 },
                        include: [{
                            kind: 'salesorder',
                            page: { limit: 2, offset: 0 }
                        }]
                    }]
                }]
            });
        }
        
        // Customer -> SalesOrder -> OrderDetail -> Product
        for (let i = 0; i < 30; i++) {
            const limits = [
                { c: 5, s: 5, o: 5, p: 5 },
                { c: 10, s: 3, o: 2, p: 1 },
                { c: 2, s: 10, o: 5, p: 3 },
                { c: 3, s: 3, o: 3, p: 3 },
                { c: 1, s: 20, o: 10, p: 5 }
            ][i % 5];
            
            this.queries.push({
                kind: 'customer',
                page: { limit: limits.c, offset: i },
                include: [{
                    kind: 'salesorder',
                    page: { limit: limits.s, offset: 0 },
                    include: [{
                        kind: 'orderdetail',
                        page: { limit: limits.o, offset: 0 },
                        include: [{
                            kind: 'product',
                            page: { limit: limits.p, offset: 0 }
                        }]
                    }]
                }]
            });
        }
        
        // Supplier -> Product -> OrderDetail -> SalesOrder
        for (let i = 0; i < 20; i++) {
            this.queries.push({
                kind: 'supplier',
                page: { limit: 2, offset: i },
                include: [{
                    kind: 'product',
                    page: { limit: 4, offset: 0 },
                    include: [{
                        kind: 'orderdetail',
                        page: { limit: 3, offset: 0 },
                        include: [{
                            kind: 'salesorder',
                            page: { limit: 2, offset: 0 }
                        }]
                    }]
                }]
            });
        }
        
        // Employee -> SalesOrder -> OrderDetail -> Product
        for (let i = 0; i < 15; i++) {
            this.queries.push({
                kind: 'employee',
                page: { limit: 2, offset: 0 },
                include: [{
                    kind: 'salesorder',
                    page: { limit: 10, offset: i * 5 },
                    include: [{
                        kind: 'orderdetail',
                        page: { limit: 5, offset: 0 },
                        include: [{
                            kind: 'product'
                        }]
                    }]
                }]
            });
        }
        
        // Mixed with no limits at various levels
        for (let i = 0; i < 35; i++) {
            const pattern = i % 4;
            if (pattern === 0) {
                // No limit at first level only
                this.queries.push({
                    kind: 'category',
                    include: [{
                        kind: 'product',
                        page: { limit: 5, offset: 0 },
                        include: [{
                            kind: 'orderdetail',
                            page: { limit: 3, offset: 0 },
                            include: [{
                                kind: 'salesorder',
                                page: { limit: 2, offset: 0 }
                            }]
                        }]
                    }]
                });
            } else if (pattern === 1) {
                // No limit at second level only
                this.queries.push({
                    kind: 'customer',
                    page: { limit: 3, offset: 0 },
                    include: [{
                        kind: 'salesorder',
                        include: [{
                            kind: 'orderdetail',
                            page: { limit: 5, offset: 0 },
                            include: [{
                                kind: 'product',
                                page: { limit: 3, offset: 0 }
                            }]
                        }]
                    }]
                });
            } else if (pattern === 2) {
                // No limit at third level only
                this.queries.push({
                    kind: 'supplier',
                    page: { limit: 2, offset: 0 },
                    include: [{
                        kind: 'product',
                        page: { limit: 4, offset: 0 },
                        include: [{
                            kind: 'orderdetail',
                            include: [{
                                kind: 'salesorder',
                                page: { limit: 3, offset: 0 }
                            }]
                        }]
                    }]
                });
            } else {
                // No limit at fourth level only
                this.queries.push({
                    kind: 'employee',
                    page: { limit: 2, offset: 0 },
                    include: [{
                        kind: 'salesorder',
                        page: { limit: 5, offset: 0 },
                        include: [{
                            kind: 'orderdetail',
                            page: { limit: 3, offset: 0 },
                            include: [{
                                kind: 'product'
                            }]
                        }]
                    }]
                });
            }
        }
        
        // Shipper -> SalesOrder -> OrderDetail -> Product
        for (let i = 0; i < 10; i++) {
            this.queries.push({
                kind: 'shipper',
                include: [{
                    kind: 'salesorder',
                    page: { limit: 30, offset: i * 10 },
                    include: [{
                        kind: 'orderdetail',
                        page: { limit: 5, offset: 0 },
                        include: [{
                            kind: 'product',
                            page: { limit: 2, offset: 0 }
                        }]
                    }]
                }]
            });
        }
    }
    
    /**
     * 6. Complex criteria queries
     */
    generateComplexCriteriaQueries() {
        // Multiple criteria on products
        for (let i = 0; i < 20; i++) {
            this.queries.push({
                kind: 'product',
                criteria: {
                    categoryId: (i % 8) + 1,
                    discontinued: '0'
                },
                page: { limit: 10, offset: 0 }
            });
        }
        
        // Complex customer queries
        const countries = ['USA', 'UK', 'Germany', 'France', 'Spain', 'Canada', 'Mexico', 'Brazil'];
        const cities = ['London', 'Paris', 'Berlin', 'Madrid', 'New York', 'Seattle', 'Portland'];
        
        for (let i = 0; i < 20; i++) {
            this.queries.push({
                kind: 'customer',
                criteria: {
                    country: countries[i % countries.length],
                    city: cities[i % cities.length]
                },
                page: { limit: 5, offset: 0 },
                include: [{
                    kind: 'salesorder',
                    page: { limit: 10, offset: 0 }
                }]
            });
        }
        
        // Orders with specific date ranges (using year as proxy)
        for (let i = 0; i < 20; i++) {
            this.queries.push({
                kind: 'salesorder',
                criteria: {
                    employeeId: (i % 9) + 1,
                    shipperid: (i % 3) + 1
                },
                page: { limit: 20, offset: i * 5 },
                include: [{
                    kind: 'orderdetail',
                    page: { limit: 10, offset: 0 }
                }]
            });
        }
        
        // Product queries with supplier and category
        for (let i = 0; i < 20; i++) {
            this.queries.push({
                kind: 'product',
                criteria: {
                    supplierId: (i % 29) + 1,
                    categoryId: (i % 8) + 1
                },
                include: [{
                    kind: 'orderdetail',
                    page: { limit: 15, offset: 0 }
                }]
            });
        }
        
        // Nested queries with criteria at multiple levels
        for (let i = 0; i < 20; i++) {
            this.queries.push({
                kind: 'category',
                criteria: { categoryId: (i % 8) + 1 },
                include: [{
                    kind: 'product',
                    criteria: { discontinued: '0' },
                    page: { limit: 10, offset: 0 },
                    include: [{
                        kind: 'orderdetail',
                        page: { limit: 5, offset: 0 }
                    }]
                }]
            });
        }
    }
    
    /**
     * 7. Mixed pagination queries
     */
    generateMixedPaginationQueries() {
        // Large limits
        for (let i = 0; i < 10; i++) {
            this.queries.push({
                kind: 'customer',
                page: { limit: 100, offset: 0 },
                include: [{
                    kind: 'salesorder',
                    page: { limit: 200, offset: 0 }
                }]
            });
        }
        
        // Very small limits (1-2)
        for (let i = 0; i < 20; i++) {
            this.queries.push({
                kind: 'category',
                page: { limit: 1, offset: i },
                include: [{
                    kind: 'product',
                    page: { limit: 2, offset: 0 },
                    include: [{
                        kind: 'orderdetail',
                        page: { limit: 1, offset: 0 }
                    }]
                }]
            });
        }
        
        // Large offset values
        for (let i = 0; i < 20; i++) {
            this.queries.push({
                kind: 'salesorder',
                page: { limit: 10, offset: i * 50 },
                include: [{
                    kind: 'orderdetail',
                    page: { limit: 5, offset: 0 }
                }]
            });
        }
        
        // Alternating limited/unlimited at each level
        for (let i = 0; i < 30; i++) {
            const useLimit1 = i % 2 === 0;
            const useLimit2 = i % 3 === 0;
            const useLimit3 = i % 5 === 0;
            
            const query = {
                kind: 'customer'
            };
            
            if (useLimit1) {
                query.page = { limit: 10, offset: 0 };
            }
            
            query.include = [{
                kind: 'salesorder'
            }];
            
            if (useLimit2) {
                query.include[0].page = { limit: 15, offset: 0 };
            }
            
            query.include[0].include = [{
                kind: 'orderdetail'
            }];
            
            if (useLimit3) {
                query.include[0].include[0].page = { limit: 20, offset: 0 };
            }
            
            this.queries.push(query);
        }
        
        // Progressive offset increments
        for (let i = 0; i < 20; i++) {
            this.queries.push({
                kind: 'product',
                page: { limit: 5, offset: i * 5 },
                include: [{
                    kind: 'orderdetail',
                    page: { limit: 10, offset: i * 2 }
                }]
            });
        }
    }
    
    /**
     * 8. Edge case queries
     */
    generateEdgeCaseQueries() {
        // Empty results expected (non-existent IDs)
        this.queries.push({
            kind: 'customer',
            criteria: { custId: 99999 },
            include: [{
                kind: 'salesorder'
            }]
        });
        
        // Very large limit
        this.queries.push({
            kind: 'orderdetail',
            page: { limit: 10000, offset: 0 }
        });
        
        // Zero offset with various limits
        for (let limit of [0, 1, 10, 100, 1000]) {
            if (limit > 0) {  // Skip limit: 0 as it's invalid
                this.queries.push({
                    kind: 'product',
                    page: { limit, offset: 0 }
                });
            }
        }
        
        // All entities with limit 1
        ['category', 'product', 'customer', 'salesorder', 'orderdetail', 'employee', 'shipper', 'supplier'].forEach(kind => {
            this.queries.push({
                kind,
                page: { limit: 1, offset: 0 }
            });
        });
        
        // Deep nesting with limit 1 at each level
        this.queries.push({
            kind: 'category',
            page: { limit: 1, offset: 0 },
            include: [{
                kind: 'product',
                page: { limit: 1, offset: 0 },
                include: [{
                    kind: 'orderdetail',
                    page: { limit: 1, offset: 0 },
                    include: [{
                        kind: 'salesorder',
                        page: { limit: 1, offset: 0 }
                    }]
                }]
            }]
        });
        
        // Multiple includes at same level (if supported)
        this.queries.push({
            kind: 'salesorder',
            page: { limit: 5, offset: 0 },
            include: [
                {
                    kind: 'orderdetail',
                    page: { limit: 10, offset: 0 }
                },
                {
                    kind: 'customer',
                    page: { limit: 1, offset: 0 }
                }
            ]
        });
        
        // Queries with unusual field combinations
        this.queries.push({
            kind: 'product',
            criteria: {
                unitsInStock: 0,
                discontinued: '1'
            },
            page: { limit: 50, offset: 0 }
        });
        
        // Cross-cutting relationships
        this.queries.push({
            kind: 'orderdetail',
            page: { limit: 100, offset: 0 },
            include: [{
                kind: 'product',
                include: [{
                    kind: 'category'
                }]
            }]
        });
        
        // Maximum depth query (5 levels if possible)
        this.queries.push({
            kind: 'category',
            page: { limit: 2, offset: 0 },
            include: [{
                kind: 'product',
                page: { limit: 3, offset: 0 },
                include: [{
                    kind: 'orderdetail',
                    page: { limit: 2, offset: 0 },
                    include: [{
                        kind: 'salesorder',
                        page: { limit: 2, offset: 0 },
                        include: [{
                            kind: 'customer',
                            page: { limit: 1, offset: 0 }
                        }]
                    }]
                }]
            }]
        });
        
        // Random edge cases
        for (let i = 0; i < 70; i++) {
            const kinds = ['category', 'product', 'customer', 'salesorder', 'orderdetail', 'employee', 'shipper', 'supplier'];
            const kind = kinds[Math.floor(Math.random() * kinds.length)];
            
            const query = { kind };
            
            // Randomly add pagination
            if (Math.random() > 0.3) {
                query.page = {
                    limit: Math.floor(Math.random() * 100) + 1,
                    offset: Math.floor(Math.random() * 200)
                };
            }
            
            // Randomly add includes
            if (Math.random() > 0.4) {
                const includeKind = kinds[Math.floor(Math.random() * kinds.length)];
                query.include = [{ kind: includeKind }];
                
                // Randomly add pagination to include
                if (Math.random() > 0.5) {
                    query.include[0].page = {
                        limit: Math.floor(Math.random() * 50) + 1,
                        offset: Math.floor(Math.random() * 100)
                    };
                }
                
                // Randomly add another level
                if (Math.random() > 0.6) {
                    const include2Kind = kinds[Math.floor(Math.random() * kinds.length)];
                    query.include[0].include = [{ kind: include2Kind }];
                    
                    if (Math.random() > 0.5) {
                        query.include[0].include[0].page = {
                            limit: Math.floor(Math.random() * 20) + 1,
                            offset: 0
                        };
                    }
                }
            }
            
            this.queries.push(query);
        }
    }
}

/**
 * Main test runner
 */
async function runTests() {
    const logger = new TestLogger();
    await logger.init();
    
    console.log(chalk.cyan('\n' + '='.repeat(80)));
    console.log(chalk.cyan.bold('NORTHWIND DATABASE COMPREHENSIVE TEST SUITE'));
    console.log(chalk.cyan('Testing 1000+ diverse queries with varying hierarchies and pagination'));
    console.log(chalk.cyan('='.repeat(80) + '\n'));
    
    // Check server health
    try {
        const health = await client.health();
        if (health.status !== 'healthy') {
            console.log(chalk.red('❌ Server is not healthy!'));
            console.log(chalk.yellow('Please start the Quarkus server:'));
            console.log(chalk.yellow('cd ../stellar-rest && mvn quarkus:dev'));
            return;
        }
        console.log(chalk.green('✓ Server is healthy\n'));
    } catch (error) {
        console.log(chalk.red('❌ Cannot connect to server!'));
        console.log(chalk.yellow('Please start the Quarkus server on port 8090'));
        return;
    }
    
    // Generate all test queries
    const generator = new QueryGenerator();
    const queries = generator.generateAll();
    
    console.log(chalk.blue(`Generated ${queries.length} test queries\n`));
    console.log(chalk.yellow('Starting test execution...\n'));
    
    // Execute queries
    for (let i = 0; i < queries.length; i++) {
        const query = queries[i];
        
        try {
            const result = await client.query(query);
            await logger.logResult(i + 1, query, true, result);
        } catch (error) {
            const errorData = error.response?.data || {};
            await logger.logResult(i + 1, query, false, null, {
                message: errorData.error || error.message,
                exception: errorData.exception,
                stackTrace: errorData.stackTrace,
                sqlState: errorData.sqlState,
                errorCode: errorData.errorCode
            });
        }
        
        // Small delay to avoid overwhelming the server
        if (i % 100 === 0 && i > 0) {
            console.log(chalk.gray(`\n--- Completed ${i} queries ---\n`));
            await new Promise(resolve => setTimeout(resolve, 100));
        }
    }
    
    // Write summary
    await logger.writeSummary();
    console.log(chalk.gray(`\nDetailed results written to: ${logFile}`));
}

// Run the tests
runTests().catch(error => {
    console.error(chalk.red('Fatal error:'), error);
    process.exit(1);
});