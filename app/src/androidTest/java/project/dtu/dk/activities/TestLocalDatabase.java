package project.dtu.dk.activities;

/**
 * Created by Bahram on 31-01-2016.
 */
import android.app.Application;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import dk.dtu.lbs.database.TrackerDataSource;
import dk.dtu.lbs.dto.GeoCoordinate;
import dk.dtu.lbs.dto.RecordHistory;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TestLocalDatabase {
    private TrackerDataSource database=null;
    private RecordHistory record=null;
    private GeoCoordinate location=null;
    @Before
    public void init() {
        database=TrackerDataSource.getInstance(InstrumentationRegistry.getTargetContext());
        record=new RecordHistory(0,123,123);
        location=new GeoCoordinate(12,12.567,54.9898);
        database.clearRecordHistoryTable();
        database.clearLocationTable();
    }
    @Test
    public void testInsertToRecordHistory(){
        database.insertToRecordHistory(record);
        assertTrue(!database.getAllRecordHistory().isEmpty());
        assertTrue("Size of records must be ", database.getAllRecordHistory().size() == 1);
    }
    @Test
    public void testGetAllRecordHistory(){
        database.insertToRecordHistory(record);
        List<RecordHistory> records=database.getAllRecordHistory();
        assertTrue(!records.isEmpty());
        assertTrue("Size of records must be 1", records.size() == 1);
        RecordHistory actual=records.get(0);
        assertTrue(actual.getFrom()==record.getFrom() && actual.getTo()==record.getTo());
    }
    @Test
    public void  testDeleteFromRecordHistory(){
        RecordHistory two=new RecordHistory(0,4545,45454);
        database.insertToRecordHistory(record);
        database.insertToRecordHistory(two);
        List<RecordHistory> records=database.getAllRecordHistory();
        assertTrue(!records.isEmpty());
        assertTrue("Size of records must be 1", records.size() == 2);
        List<Long> ids=new ArrayList<>();
        ids.add(records.get(0).getRid());
        ids.add(records.get(1).getRid());
        database.deleteFromRecordHistory(ids);
        assertTrue(database.getAllRecordHistory().isEmpty());
    }

    @Test
    public void testInsertIntoLocation(){
        database.insertLocation(location);
        List<GeoCoordinate> locations=database.getAllLocations();
        assertTrue("Location table must not be empty", !locations.isEmpty());
        assertTrue("Number of GeoCoordinate should be 1",locations.size()==1);

    }
    /*This test a copy of test insert into location. */
    @Test
    public void testGetAllLocations(){
        database.insertLocation(location);
        List<GeoCoordinate> locations=database.getAllLocations();
        assertTrue("Location table must not be empty",!locations.isEmpty());
        assertTrue("Number of GeoCoordinate should be 1",locations.size()==1);

    }
    @Test
    public void testDeleteLocationsInInterval(){
        GeoCoordinate two=new GeoCoordinate(126,123,123);
        GeoCoordinate three=new GeoCoordinate(127,123,123);
        GeoCoordinate four=new GeoCoordinate(128,123,123);
        database.insertLocation(location);
        database.insertLocation(two);
        database.insertLocation(three);
        database.insertLocation(four);
        List<GeoCoordinate> locations=database.getAllLocations();
        assertTrue("Location table must not be empty",!locations.isEmpty());
        assertTrue("Number of GeoCoordinate should be 4",locations.size()==4);
        database.deleteLocationInTimeInterval(location.getTime(), four.getTime());
        assertTrue("Location table should be empty : "+database.getAllLocations().size(),database.getAllLocations().isEmpty());

    }



}
