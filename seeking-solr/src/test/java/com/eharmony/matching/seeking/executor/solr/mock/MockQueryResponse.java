package com.eharmony.matching.seeking.executor.solr.mock;

import java.util.List;

import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.eharmony.matching.seeking.executor.solr.SolrResultsTransformer;

public class MockQueryResponse extends QueryResponse {

    private static final long serialVersionUID = 6048360672523443191L;

    private final DocumentObjectBinder binder = new DocumentObjectBinder();
    private final SolrDocumentList documents;

    public MockQueryResponse(Object... beans) {
        documents = new SolrDocumentList();
        for (Object o : beans) {
            if (SolrResultsTransformer.isValueType(o.getClass())) {
                SolrDocument document = new SolrDocument();
                document.addField("something", o);
                documents.add(document);
            } else {
                documents.add(ClientUtils.toSolrDocument(binder
                        .toSolrInputDocument(o)));
            }
        }
    }

    @Override
    public <R> List<R> getBeans(Class<R> type) {
        return binder.getBeans(type, documents);
    }
    
    @Override
    public SolrDocumentList getResults() {
        return documents;
    }

}
