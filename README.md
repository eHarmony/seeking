[![Build Status](https://travis-ci.org/eHarmony/seeking.svg?branch=master)](https://travis-ci.org/eHarmony/seeking)

# Seeking

Seeking is library for generically building and executing type safe queries against different data stores.

Implementations are currently available for:

* Any relational database management system (RDBMS) supported by [Hibernate](http://hibernate.org/)
* [MongoDB](http://www.mongodb.org/)
* [Solr](http://lucene.apache.org/solr/)

Seeking allows you to decouple your query building logic from data store APIs making it possible to switch between multiple different data stores without needing to recreate your queries.


## Query Building

A better DSL for Query building is under development but, for now, we will use a combination of the QueryBuilder and the static, Hibernate-style Restrictions methods to construct our queries.

Suppose we have the following TestClass we want to query against in our data store:

```java
  public class TestClass {
      @Id
      private Long id;
      private String name;
      private Date date;
      private LatLon latLon;
  }

  public class LatLon {
      @XField
      private double lat;
      @YField
      private double lon;
  }
```

### Simple Queries

Construct a query to find items with a date older than a day ago as follows:

```java
  import com.eharmony.matching.seeking.query.builder.QueryBuilder;
  import static com.eharmony.matching.seeking.query.criterion.Restrictions.*;
  import org.joda.time.DateTime;

  final Query<TestClass, TestClass> query = QueryBuilder
              .builderFor(TestClass.class)
              .add(lt("date", DateTime.now().minusDays(1).toDate()))
              .build();
```

### Compound Queries

Construct a more complex query where not only do we want to find items with a date older than a day ago, but also those who are within one degree of the provided latLon OR whose names are in the provided favorites list:

```java
  //provided
  LatLon latLon = getLatLon();
  List<String> favorites = getFavorites();

  final Query<TestClass, TestClass> query = QueryBuilder
              .builderFor(TestClass.class)
              .add(lt("date", DateTime.now().minusDays(1).toDate()))
              .add(or(
                within("latLon", latLon.getLat(), latLon.getLon(), 1D),
                in("name", favorites)
              )).build();
```

*Note:* by default, expressions will be ANDed together when added separately.

### Querying Nested Properties

It is possible to query using nested properties.  Get the ids of everybody in the latitudes encompassing Antarctica:

```java
  final Query<TestClass, TestClass> query = QueryBuilder
              .builderFor(TestClass.class, Long.class, "id")
              .add(lt("latlon.lat", -60.0D))
              .build();
```

### Returning Alternate Types

Return only the id of those objects being queried.

```java
  final Query<TestClass, TestClass> query = QueryBuilder
              .builderFor(TestClass.class, Long.class, "id")
              .add(lt("date", DateTime.now().minusDays(1).toDate()))
              .build();
```

Return a slimmer version of TestClass that only contains the id and the name:

```java
  public class TinyTestClass {
      @Id
      private Long id;
      private String name;
  }
  
  
  final Query<TestClass, TinyTestClass> query = QueryBuilder
              .builderFor(TestClass.class, Long.class, "id", "name")
              .add(lt("date", DateTime.now().minusDays(1).toDate()))
              .build();
```

*Note:* we could fetch the entire TestClass entity and ignore the unused fields when mapping to the smaller type but returning only what we need is more efficient.


### Query Components
The following query components are supported:

```java
  // equals
  EqualityExpression eq(String propertyName, Object value);

  // does not equal (not equals);
  EqualityExpression ne(String propertyName, Object value);

  // less than
  EqualityExpression lt(String propertyName, Object value);

  // less than or equal
  EqualityExpression lte(String propertyName, Object value);

  // greater than
  EqualityExpression gt(String propertyName, Object value);

  // greater than or equal
  EqualityExpression gte(String propertyName, Object value);

  // between from and to (inclusive)
  RangeExpression between(String propertyName, Object from, Object to);

  // discrete range - converts from..to to a discrete range of integers
  //    ex: 1..5 becomes in:[1,2,3,4,5]
  SetExpression discreteRange(String propertyName, int from, int to);

  // in the set
  SetExpression in(String propertyName, Object[] values);
  SetExpression in(String propertyName, Collection<? extends Object> values);

  // not in the set
  SetExpression notIn(String propertyName, Object[] values);
  SetExpression notIn(String propertyName, Collection<? extends Object> values);

  // set contains (Note: this is not currently supported for all data stores)
  SetExpression contains(String propertyName, Object value);
  SetExpression contains(String propertyName, Object[] values);

  // is null
  UnaryExpression isNull(String propertyName);

  // is not null
  UnaryExpression isNotNull(String propertyName);

  // is empty
  UnaryExpression isEmpty(String propertyName);

  // is not empty
  UnaryExpression isNotEmpty(String propertyName);

  // native, datas store specific query for cases not covered here
  <T> NativeExpression nativeQuery(Class<T> type, T expression);
  
  // and - takes a variable list of expressions as arguments
  Conjunction and(Criterion... criteria);

  // or - takes a variable list of expressions as arguments
  Disjunction or(Criterion... criteria);

  // within the distance from the center point (x,y)
  // relies either on build in data store implementation or Seeking's box approximations
  <N extends Number & Comparable<N>> Distance2dExpression<N> within(String propertyName, N x, N y, N distance);
```

### Resolving Entity and Property Names

Always use the property names of your Java objects in your queries.
If these names differ from those used in your datastore you will use annotations to provide the mappings.
Entity Resolvers are configured to map the entity classes to table/collection names.
Property Resolvers are configured to map the names of your object variables to column/field names.

The following annotations are currently supported for the indicated data store type.
Custom EntityResolvers and PropertyResolvers are easy to configure and create.

|             | Entity Annotation(s)                                                             | Field Annotation(s)                                                                                    |
| ----------- | -------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------ |
| **RDBMS**   | @javax.persistence.Table or @javax.persistence.Entity                            | see [Hibernate Annotations](http://docs.jboss.org/hibernate/annotations/3.5/reference/en/html_single/) |
| **MongoDB** | see [Morphia Annotations](https://code.google.com/p/morphia/wiki/AllAnnotations) | see [Morphia Annotations](https://code.google.com/p/morphia/wiki/AllAnnotations)                       |
| **Solr**    | N/A                                                                              | @org.apache.solr.client.solrj.beans.Field                                                              |


## Query Execution

The QueryExecutor interface supports the following operations:

```java
  // return an iterable of type R from the query against type T (R and T will often be the same type)
  <T, R> Iterable<R> find(Query<T, R> query);
  
  // return aone R from the query against type T
  <T, R> R findOne(Query<T, R> query);
  
  // save the entity of type T to the data store
  <T> T save(T entity);
  
  // save all of the entities in the provided iterable to data store
  <T> Iterable<T> save(Iterable<T> entities);
```

*Note:* Future versions of this library may instead consume and produce Iterators rather than Iterables.


## Configuration

Here are some example Spring configuration files for the different data stores.

### Hibernate
```xml
    <!-- Property Resolver -->
    <bean id="hibernatePropertyResolver" class="com.eharmony.matching.seeking.translator.hibernate.SimpleHibernatePropertyResolver"/>
    
    <!-- Box Maker (for "within" queries) -->
    <bean id="boxMaker" class="com.eharmony.matching.seeking.query.geometry.SimpleBoxMaker" />
    
    <!-- Query Translator -->
    <bean id="hibernateQueryTranslator" class="com.eharmony.matching.seeking.translator.hibernate.HibernateQueryTranslator">
      <constructor-arg ref="hibernatePropertyResolver" />
      <constructor-arg ref="boxMaker" />
    </bean>
    
    <!-- Query Executor - here we're using the IterativeHibernateQueryExecutor which allow us to fetch results in batches -->
    <bean id="hibernateQueryExecutor" class="com.eharmony.matching.seeking.executor.hibernate.IterativeHibernateQueryExecutor">
      <constructor-arg ref="sessionFactory" />
      <constructor-arg ref="hibernateQueryTranslator" />
    </bean>
    
    <!-- Hibernate Session Factory -->
    <bean id="sessionFactoryBean" class="com.eharmony.cmp.common.hibernate.SessionFactoryBean">
      <constructor-arg>
        <value>hibernate.cfg.xml</value>
      </constructor-arg>
    </bean>
    
    <bean id="sessionFactory" factory-bean="sessionFactoryBean" factory-method="getInstance"/>
    
```

### MongoDB

```xml
    <!-- Property Resolver -->
    <bean id="mongoPropertyResolver" class="com.eharmony.matching.seeking.translator.mongodb.MorphiaPropertyResolver"/>
    
    <!-- Entity Resolver -->
    <bean id="mongoEntityResolver" class="com.eharmony.matching.seeking.translator.mongodb.MorphiaEntityResolver"/>
    
    <bean id="mongoQueryTranslator" class="com.eharmony.matching.seeking.translator.mongodb.MongoQueryTranslator">
      <constructor-arg ref="mongoPropertyResolver" />
    </bean>
    
    <bean id="mongoExecutor" class="com.eharmony.matching.seeking.executor.mongodb.MongoQueryExecutor">
      <constructor-arg name="mongo" ref="mongo" />
      <constructor-arg name="database" value="${your.config.mongo.database}" />
      <constructor-arg name="writeConcern" ref="mongoWriteConcern" />
      <constructor-arg name="queryTranslator" ref="mongoQueryTranslator" />
      <constructor-arg name="entityResolver" ref="mongoEntityResolver" />
    </bean>
    
    <bean id="mongo" class="com.mongodb.Mongo">
      <constructor-arg value="${your.config.mongo.server}" />
      <constructor-arg ref="mongoOptions" />
      <property name="readPreference">
        <bean class="com.mongodb.ReadPreference" factory-method="secondaryPreferred"/>
      </property>
    </bean>
    
    <!-- Mongo Options -->
    <bean id="mongoOptions" class="com.mongodb.MongoOptions">
      <property name="socketKeepAlive" value="true" />
      <property name="connectionsPerHost" value="${your.config.mongo.connectionsPerHost}" />
    </bean>
    
    <!-- Mongo Write Concern -->
    <bean id="mongoWriteConcern" class="com.mongodb.WriteConcern">
      <constructor-arg name="w" value="${your.config.mongo.writerReplication}" />
      <constructor-arg name="wtimeout" value="${your.config.mongo.writeTimeout}" />
      <constructor-arg name="fsync" value="${your.config.mongo.waitForFsync}" />
      <constructor-arg name="j" value="${your.config.mongo.waitForJournal}" />
      <constructor-arg name="continueOnInsertError" value="${your.config.mongo.continueOnInsertError}" />
    </bean>

```

### Solr

Configuration for Solr is similar to the previous two examples.  Please consult the source code in seeking-solr for the parameters involved in creating a SolrQueryTranslator and SolrQueryExecutor.

The Solr implementation for Seeking has been thoroughly tested but, unlike the Hibernate and MongoDB implementations, has not been used by us in production and therefore may not be as stable.

Additionally, the Solr implementation does not currently work with hierarchical objects (objects with other nested objects) due to the limitations of Solr itself.
There are tentative plans to develop a method for "flattening" nested objects into a single document (though not collections of custom objects) but that is not currently being developed.



