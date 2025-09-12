import axios from 'axios';

/**
 * Stellar REST Client
 * Wrapper around the Stellar REST API for sending queries
 */
export class StellarClient {
    constructor(baseURL = 'http://localhost:8090/api') {
        this.client = axios.create({
            baseURL,
            headers: {
                'Content-Type': 'application/json'
            },
            timeout: 10000
        });
    }
    
    /**
     * Execute a query against the Stellar REST API
     */
    async query(queryObject) {
        try {
            const response = await this.client.post('/query', queryObject);
            return response.data;
        } catch (error) {
            if (error.response) {
                throw new Error(`API Error: ${error.response.data.error || error.message}`);
            }
            throw error;
        }
    }
    
    /**
     * Health check
     */
    async health() {
        try {
            const response = await this.client.get('/health');
            return response.data;
        } catch (error) {
            return { status: 'unhealthy', error: error.message };
        }
    }
    
    /**
     * Query by entity kind
     */
    async queryByKind(kind, queryObject) {
        try {
            const response = await this.client.post(`/${kind}`, queryObject);
            return response.data;
        } catch (error) {
            if (error.response) {
                throw new Error(`API Error: ${error.response.data.error || error.message}`);
            }
            throw error;
        }
    }
}

/**
 * Query builder helper functions
 */
export const QueryBuilder = {
    /**
     * Create a simple query
     */
    simple(kind, criteria = {}, page = null) {
        const query = { kind, criteria };
        if (page) {
            query.page = page;
        }
        return query;
    },
    
    /**
     * Create a query with nested includes
     */
    withIncludes(kind, criteria = {}, includes = [], page = null) {
        const query = this.simple(kind, criteria, page);
        if (includes.length > 0) {
            query.include = includes;
        }
        return query;
    },
    
    /**
     * Create a page object
     */
    page(limit, offset = 0) {
        return { limit, offset };
    }
};