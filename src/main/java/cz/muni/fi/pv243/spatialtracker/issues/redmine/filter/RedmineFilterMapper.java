
package cz.muni.fi.pv243.spatialtracker.issues.redmine.filter;

import cz.muni.fi.pv243.spatialtracker.issues.filter.IssueFilter;

public interface RedmineFilterMapper<T extends IssueFilter> {

    Class<T> targetClass();

    String filterFragmentFrom(T filter);

}