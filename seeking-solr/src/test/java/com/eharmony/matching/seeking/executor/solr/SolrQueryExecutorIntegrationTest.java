package com.eharmony.matching.seeking.executor.solr;

import static com.eharmony.matching.seeking.query.criterion.Restrictions.eq;
import static com.eharmony.matching.seeking.query.criterion.Restrictions.gte;
import static com.eharmony.matching.seeking.query.criterion.Restrictions.in;
import static com.eharmony.matching.seeking.query.criterion.Restrictions.nativeQuery;
import static com.eharmony.matching.seeking.query.criterion.Restrictions.within;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.eharmony.matching.seeking.query.builder.QueryBuilder;
import com.eharmony.matching.seeking.query.criterion.Ordering;
import com.eharmony.matching.seeking.query.criterion.Restrictions;
import com.eharmony.matching.seeking.test.UserIndex;
import com.eharmony.matching.seeking.test.UserIndexBuilder;
import com.eharmony.matching.seeking.translator.solr.SolrQueryTranslator;
import com.eharmony.matching.seeking.translator.solr.SolrjPropertyResolver;
import com.google.common.collect.Lists;

public class SolrQueryExecutorIntegrationTest {

    public static class LittleUserIndex {
        @Field("user_id")
        private int userId;
        @Field("birth_date")
        private Date birthDate;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public Date getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(Date birthDate) {
            this.birthDate = birthDate;
        }
        
        public boolean isEquivalentTo(UserIndex u) {
            return userId == u.getUserId() && birthDate.equals(u.getBirthDate());
        }
    }

    private final Class<UserIndex> entityClass = UserIndex.class;

    private final SolrServer solrServer;
    private final SolrQueryTranslator queryTranslator;
    private final SolrQueryExecutor executor;

    public SolrQueryExecutorIntegrationTest() throws Exception {
        String solrHome = getClass().getResource("/solr").getFile();
        File solrConfigXml = new File(solrHome + "/solr.xml");
        CoreContainer coreContainer = new CoreContainer(solrHome, solrConfigXml);
        solrServer = new EmbeddedSolrServer(coreContainer, "user");
        queryTranslator = new SolrQueryTranslator(new SolrjPropertyResolver());
        executor = new SolrQueryExecutor(solrServer, queryTranslator);
    }

    private final UserIndex user = new UserIndexBuilder().id(42).build();
    private final List<UserIndex> users = Arrays.asList(new UserIndexBuilder()
            .id(0).age(55).build(), new UserIndexBuilder().id(1).age(50)
            .build(), new UserIndexBuilder().id(2).age(45).build(),
            new UserIndexBuilder().id(3).age(40).build(),
            new UserIndexBuilder().id(4).age(35).build(),
            new UserIndexBuilder().id(5).age(30).build(),
            new UserIndexBuilder().id(6).age(25).build(),
            new UserIndexBuilder().id(7).age(20).build());
    private final Integer[] primeIds = new Integer[] { 1, 3, 5, 7 };

    @Before
    public void before() throws SolrServerException, IOException {
        executor.save(users);
        solrServer.commit();
    }

    @After
    public void after() throws SolrServerException, IOException {
        for (UserIndex u : users) {
            solrServer.deleteById(u.getIdString());
        }
        solrServer.commit();
        solrServer.shutdown();
    }

    @Test
    public void saveAndFind() throws SolrServerException, IOException {
        executor.save(user);
        solrServer.commit();
        UserIndex found = executor.findOne(QueryBuilder
                .builderFor(entityClass)
                .add(Restrictions.eq("user_id", user.getUserId())).build());
        assertEquals(user, found);
        solrServer.deleteById(user.getIdString());
    }

    @Test
    public void find_byId_one() {
        int id = 3;
        Iterable<UserIndex> found = executor.find(QueryBuilder
                .builderFor(entityClass).add(eq("user_id", id)).build());
        assertEquals(users.get(id), found.iterator().next());
    }

    @Test
    public void find_byIds() {
        Iterable<UserIndex> found = executor.find(QueryBuilder
                .builderFor(entityClass).add(in("user_id", primeIds))
                .addOrder(Ordering.asc("user_id")).build());
        ArrayList<UserIndex> list = Lists.newArrayList(found);
        assertEquals(4, list.size());
        for (int i = 0; i < primeIds.length; i++) {
            assertEquals(users.get(primeIds[i]), list.get(i));
        }
    }

    @Test
    public void find_byIdsAsIntegers() {
        Iterable<Integer> found = executor.find(QueryBuilder
                .builderFor(entityClass, Integer.class, "user_id")
                .add(in("user_id", primeIds)).addOrder(Ordering.asc("user_id"))
                .build());
        ArrayList<Integer> list = Lists.newArrayList(found);
        assertEquals(4, list.size());
        for (int i = 0; i < primeIds.length; i++) {
            assertEquals(primeIds[i], list.get(i));
        }
    }
    
    @Test
    public void find_byIdsAsLittleUserIndex() {
        Iterable<LittleUserIndex> found = executor.find(QueryBuilder
                .builderFor(entityClass, LittleUserIndex.class)
                .add(in("user_id", primeIds)).addOrder(Ordering.asc("user_id"))
                .build());
        ArrayList<LittleUserIndex> list = Lists.newArrayList(found);
        assertEquals(4, list.size());
        for (int i = 0; i < primeIds.length; i++) {
            assertTrue(list.get(i).isEquivalentTo(users.get(primeIds[i])));
        }
    }
    
    @Test
    public void find_byDateRange() {
        int age = 31;
        Date birthdate = DateTime.now().minusYears(age).toDate();
        // find younger
        Iterable<UserIndex> found = executor.find(QueryBuilder
                .builderFor(entityClass).add(gte("birthDate", birthdate))
                .addOrder(Ordering.desc("birthDate")).build());
        ArrayList<UserIndex> list = Lists.newArrayList(found);
        assertEquals(3, list.size());
        assertEquals(users.get(7), list.get(0));
        assertEquals(users.get(6), list.get(1));
        assertEquals(users.get(5), list.get(2));
    }
    
    
    @Test
    public void find_byDistance() {
        Iterable<UserIndex> found = executor.find(QueryBuilder
                .builderFor(entityClass).add(within("location", 34.028, -118.474, 1.0))
                .build());
        ArrayList<UserIndex> list = Lists.newArrayList(found);
        assertEquals(8, list.size());
    }
    
    @Test
    public void find_byDistance_noResults() {
        Iterable<UserIndex> found = executor.find(QueryBuilder
                .builderFor(entityClass)
                .add(within("location", 32.028, -118.474, 1.0))
                .build());
        ArrayList<UserIndex> list = Lists.newArrayList(found);
        assertEquals(0, list.size());
    }
    
    @Test
    public void find_byDateRangeAndDistance() {
        int age = 31;
        Date birthdate = DateTime.now().minusYears(age).toDate();
        // find younger
        Iterable<UserIndex> found = executor.find(QueryBuilder
                .builderFor(entityClass)
                .add(gte("birthDate", birthdate))
                .add(within("location", 34.028, -118.474, 1.0))
                .addOrder(Ordering.desc("birthDate")).build());
        ArrayList<UserIndex> list = Lists.newArrayList(found);
        assertEquals(3, list.size());
        assertEquals(users.get(7), list.get(0));
        assertEquals(users.get(6), list.get(1));
        assertEquals(users.get(5), list.get(2));
    }
    
    @Test
    public void find_byId_one_NativeQuery() {
        int id = 3;
        Iterable<UserIndex> found = executor.find(QueryBuilder
                .builderFor(entityClass)
                .add(nativeQuery(String.class, "user_id:" + id))
                .build());
        assertEquals(users.get(id), found.iterator().next());
    }

}
