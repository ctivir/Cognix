/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cognitivabrasil.repositorio.models;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import javax.sql.DataSource;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.SortedTable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Ignore;

/**
 * This class serves as base class for the ServiceTests.
 *
 * Ir provides the DBUnit helper functions common to all DAO tests.
 *
 *
 * @author Paulo Schreiner <paulo@jorjao81.com>
 */
@Ignore
public abstract class AbstractServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    DataSource dataSource;
    static IDatabaseConnection connection;
    boolean updated = false;

    /**
     * This method releases all resources allocated to the ServiceTest
     * (i.g, connections)
     */
//    @AfterClass
//    public static void tearDownClass() throws Exception {
//        connection.close();
//    }

    /**
     * Runs before each test, inserts data set into database
     */
    @Before
    public void init() throws Exception {
        // Insere os dados no banco de dados
        DatabaseOperation.CLEAN_INSERT.execute(getConnection(), getBeforeDataSet());
    }

    /**
     * 
     * @return Connection to the database
     */
    protected IDatabaseConnection getConnection() throws Exception {
        // Pega a conexão com o banco de dados
        if (connection == null) {
            Connection con = dataSource.getConnection();
            DatabaseMetaData databaseMetaData = con.getMetaData();
            connection = new DatabaseConnection(con);
        }

        return connection;

    }

    /**
     * 
     * @return Data befora
     */
    protected IDataSet getBeforeDataSet() throws Exception {
        File file = new File("src/test/resources/databaseDataBefore.xml");
        return new FlatXmlDataSet(file);
    }

    /**
     * 
     * @return Data after changes
     */
    protected IDataSet getAfterDataSet() throws Exception {
        File file = new File("src/test/resources/documentosDataAfter.xml");
        return new FlatXmlDataSet(file);
    }
}
