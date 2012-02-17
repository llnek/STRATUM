# About
Yet another simple java object relational database persistence library based on Annotations.</br>Supports inheritance and basic relations { 1 to 1, 1 to Many and Many to Many }.

# Supported Platforms

## Java
* &gt;= 1.6

## Databases
* Microsoft SQL Server
* MySQL
* Oracle
* PostgreSQL
* HSQLDB
* H2

# Define a class
<pre>
@Table(table="TABLE_SOMECLASS")
public class SomeClass implements StratumObj {
private String a_string;

    // defines a db column via getter
    @Column(id="A_STRING")
    public String getAString() { return a_string; }
    // must be paired with mutator
    // NOTE: method naming convention { getXXX setXXX }
    public void setAString(String s) { a_string=s; }

}
</pre>

# Generate & load DDL
<pre>
String ddl= d.getDDL(Person.class, Employee.class, 
        Address.class, Company.class, Department.class) ;
JDBCInfo props=JDBCInfo("org.h2.Driver","jdbc:h2:/tmp/h2db", "sa", "secret");
DDLUte.loadDDL(props, ddl) ;

</pre>

# Initialize and connect to Data Source
<pre>
JDBCInfo info=JDBCInfo("org.h2.Driver","jdbc:h2:/tmp/h2db", "sa", "secret");
Stratum s= new Stratum(info);
DBIO db=s.openDB();
....
s.finz(); // close out everything
</pre>

# CRUD some object
<pre>
Employee emp=new Employee();
emp.setXXX() ....
...
db.startTransaction();
db.create(emp);
db.commit();

// get object where LOGIN='joeb'
emp= db.fetchObj(Employee.class, new NameValues( "LOGIN", "joeb"));
db.startTransaction();
emp.setIQ(43);
db.update(emp) ;
db.commit();

db.startTransaction();
db.remove(emp) ;
db.commit();

emp= _db.fetchObj(Employee.class, new NameValues( "LOGIN", "joeb"));
assert(emp == null);
</pre>


# Latest binary
Download the latest bundle [1.0.0](http://www.zotoh.com/packages/stratum/stable/1.0.0/stratum-1.0.0.zip)



