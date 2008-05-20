/**
 *    This module represents an engine IMPL for the load testing framework
 *    Copyright (C) 2008  Imran M Yousuf (imran@smartitengineering.com)
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.smartitengineering.loadtest.engine.impl.persistence.filesystem.xml;

import com.smartitengineering.loadtest.engine.persistence.PersistentTestResultEngine;
import com.smartitengineering.loadtest.engine.result.KeyedInformation;
import com.smartitengineering.loadtest.engine.result.TestCaseInstanceResult;
import com.smartitengineering.loadtest.engine.result.TestCaseResult;
import com.smartitengineering.loadtest.engine.result.TestProperty;
import com.smartitengineering.loadtest.engine.result.TestResult;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author imyousuf
 */
public class XMLPersistentTestResultEngineImpl
    implements PersistentTestResultEngine {

    private String testResultSourceDir;

    protected XMLPersistentTestResultEngineImpl(String sourceDirectory) {
        if (sourceDirectory == null) {
            throw new IllegalArgumentException();
        }
        testResultSourceDir = sourceDirectory;
    }

    public List<TestResult> getAllResults() {
        File sourceDir = new File(testResultSourceDir);
        if (sourceDir.isDirectory()) {
            File[] xmlFiles = sourceDir.listFiles(new FilenameFilter() {

                public boolean accept(File dir,
                                      String name) {
                    return name.endsWith(".xml");
                }
            });
            ArrayList<TestResult> allResults = new ArrayList<TestResult>(
                xmlFiles.length);
            try {
                JAXBContext jc = JAXBContext.newInstance(TestResult.class,
                    TestCaseResult.class, TestProperty.class,
                    TestCaseInstanceResult.class, KeyedInformation.class);
                Unmarshaller unmarshaller = jc.createUnmarshaller();
                for (File xmlFile : xmlFiles) {
                    try {
                        JAXBElement<TestResult> root = unmarshaller.<TestResult>
                            unmarshal(
                            new StreamSource(xmlFile),
                            TestResult.class);
                        TestResult unmarshalledTestResult = root.getValue();
                        allResults.add(unmarshalledTestResult);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                        return allResults;
                    }
                }
                return allResults;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return allResults;
            }
        }
        return Collections.<TestResult>emptyList();
    }

    public List<TestResult> getAllForTestName(String testName) {
        List<TestResult> testResults = getAllResults();
        if (testName == null) {
            return testResults;
        }
        return getTestResultsForName(testResults, testName);
    }

    public TestResult getTestResultById(int testResultId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TestResult> getAllResultWithinDateRange(Date startDate,
                                                        Date endDate) {
        return getAllResultsWithinDateRange(getAllResults(), startDate, endDate);
    }

    public List<TestResult> getAllResultWithinDateRange(String testName,
                                                        Date startDate,
                                                        Date endDate) {
        return getAllResultsWithinDateRange(getTestResultsForName(
            getAllResults(), testName), startDate, endDate);
    }

    public List<TestResult> getTestResults(Map<String, ? extends Object> filters)
        throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected List<TestResult> getAllResultsWithinDateRange(
        List<TestResult> testResults,
        Date startDate,
        Date endDate) {
        ArrayList<TestResult> filteredResults =
            new ArrayList<TestResult>();
        for (TestResult testResult : testResults) {
            if (testResult.getStartDateTime().after(startDate) || testResult.
                getEndDateTime().before(endDate)) {
                filteredResults.add(testResult);
            }
        }
        return filteredResults;
    }

    protected List<TestResult> getTestResultsForName(
        List<TestResult> testResults,
        String testName) {
        ArrayList<TestResult> filteredResults =
            new ArrayList<TestResult>();
        for (TestResult testResult : testResults) {
            if (testResult.getTestName().equals(testName)) {
                filteredResults.add(testResult);
            }
        }
        return filteredResults;
    }
}
