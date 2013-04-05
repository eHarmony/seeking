/*
 *  Copyright 2012 eHarmony, Inc
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.eharmony.matching.seeking.executor.solr;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;

import com.eharmony.matching.seeking.executor.QueryExecutor;
import com.eharmony.matching.seeking.query.Query;
import com.eharmony.matching.seeking.translator.solr.SolrOrdering;
import com.eharmony.matching.seeking.translator.solr.SolrQueryTranslator;
import com.eharmony.matching.seeking.translator.solr.SolrSpatialQuery;

/**
 * A QueryExecutor implementation that interacts with Solr
 * 
 * NOTE: the SolrServer is tied to the collection/core so, unlike the Hibernate
 * and MongoDB implementations, you must use a distinct SolrQueryExecutor per
 * entity type being queried. This explains the absence of a
 * SolrjEntityResolver.
 */
public class SolrQueryExecutor implements QueryExecutor {

    private final SolrQueryTranslator queryTranslator;
    private final SolrServer solrServer;
    private final SolrResultsTransformer transformer = new SolrResultsTransformer();
    
    public SolrQueryExecutor(SolrServer solrServer,
            SolrQueryTranslator queryTranslator) {
        this.queryTranslator = queryTranslator;
        this.solrServer = solrServer;
    }
    
    protected <T, R> SolrQuery translate(Query<T, R> query) {
        SolrQuery solrQuery = new SolrQuery();
        // special geospatial query case
        SolrSpatialQuery spatialQuery = queryTranslator.getSpatialQuery(query);
        String translated = queryTranslator.translate(query);
        solrQuery.setQuery(translated == null || translated.isEmpty() ? "*:*" : translated);
        if (spatialQuery != null) {
            //solrQuery.addFilterQuery("{!func}geodist()"); 
            solrQuery.addFilterQuery("{!geofilt}");
            solrQuery.set("sfield", spatialQuery.getField()); 
            solrQuery.set("pt", spatialQuery.getX() + "," + spatialQuery.getY());
            solrQuery.set("d", spatialQuery.getDistance().toString());
        }
        for (SolrOrdering ordering : queryTranslator.translateOrder(query).get()) {
            String field = ordering.getField();
            if (spatialQuery != null && field.equals(spatialQuery.getField())) {
                field = "geodist()";
            }
            solrQuery.addSortField(field, ordering.getOrder());
        }
        for (String field : queryTranslator.translateProjection(query)) {
            solrQuery.addField(field);
        }
        if (query.getMaxResults() != null) {
            solrQuery.setRows(query.getMaxResults());
        }
        return solrQuery;
    }
    
    protected <T, R> List<R> fetch(SolrQuery solrQuery, Class<R> returnType) {
        try {
            return transformer.transform(solrServer.query(solrQuery)
                    .getResults(), returnType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public <T, R> Iterable<R> find(Query<T, R> query) {
        return fetch(translate(query), query.getReturnType());
    }

    @Override
    public <T, R> R findOne(Query<T, R> query) {
        SolrQuery solrQuery = translate(query);
        solrQuery.setRows(1);
        List<R> list = fetch(solrQuery, query.getReturnType());
        return list.size() > 0 ? list.get(0) : null;
    }
    
    public <T> T save(T entity) {
        try {
            solrServer.addBean(entity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return entity;
    }
    
    public <T> Iterable<T> save(Iterable<T> entities) {
        List<T> saved = new ArrayList<T>();
        for (T entity : entities) {
            saved.add(save(entity));
        }
        return saved;
    }

}
