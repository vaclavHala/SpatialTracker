package cz.muni.fi.pv243.spatialtracker.issues.redmine.filter;

import cz.muni.fi.pv243.spatialtracker.config.Property;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.INTEGRATION_PROJECT_ID;
import cz.muni.fi.pv243.spatialtracker.issues.filter.IssueFilter;
import java.util.*;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * Redmine does not support 'set membership' filtering for ids.
 * The solution used here is to create one query for each combination
 * of queried id-type filter.
 *
 * Filter operations supported by type, copied from Redmine sourcefor reference:
 * ({@code http://www.redmine.org/projects/redmine/repository/entry/trunk/app/models/query.rb#L210})
 * <pre>{@code
 * :list => [ "=", "!" ],
 * :list_status => [ "o", "=", "!", "c", "*" ],
 * :list_optional => [ "=", "!", "!*", "*" ],
 * :list_subprojects => [ "*", "!*", "=" ],
 * :date => [ "=", ">=", "<=", "><", "<t+", ">t+", "><t+", "t+", "t", "ld", "w", "lw", "l2w", "m", "lm", "y", ">t-", "<t-", "><t-", "t-", "!*", "*" ],
 * :date_past => [ "=", ">=", "<=", "><", ">t-", "<t-", "><t-", "t-", "t", "ld", "w", "lw", "l2w", "m", "lm", "y", "!*", "*" ],
 * :string => [ "=", "~", "!", "!~", "!*", "*" ],
 * :text => [  "~", "!~", "!*", "*" ],
 * :integer => [ "=", ">=", "<=", "><", "!*", "*" ],
 * :float => [ "=", ">=", "<=", "><", "!*", "*" ],
 * :relation => ["=", "=p", "=!p", "!p", "*o", "!o", "!*", "*"],
 * :tree => ["=", "~", "!*", "*"]
 * }</pre>
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class RedmineFilterComposer {

    private final Map<Class, RedmineFilterMapper> filterTranslators;

    @Inject
    public RedmineFilterComposer(
            final @Any Instance<RedmineFilterMapper<?>> filterMappers) {
        this.filterTranslators = new HashMap<>();
        for (RedmineFilterMapper<?> mapper : filterMappers) {
            this.filterTranslators.put(mapper.targetClass(), mapper);
        }
    }

    public String assembleFilters(final Collection<IssueFilter> filters) {
        List<String> queryFragments = new ArrayList<>();
        for (IssueFilter filter : filters) {
            RedmineFilterMapper<IssueFilter> translator = this.filterTranslators.get(filter.getClass());
            if (translator == null) {
                throw new IllegalStateException("Mapper for type " + filter.getClass() + " was not registered");
            }
            queryFragments.add(translator.filterFragmentFrom(filter));
        }
        return String.join("&", queryFragments);
    }

    /**
     *
     * @param filters
     * @return set of queries which need to be invoked and added together
     * to satisfy the query described by {@code filters}
     */
    //    public List<String> assembleFilters(final Collection<IssueFilter> filters) {
    //        List<QueryBuilder> queries = new ArrayList<>();
    //        QueryBuilder initialEmptyQuery = new QueryBuilder();
    //        queries.add(initialEmptyQuery);
    //        for (IssueFilter filter : filters) {
    //             RedmineFilterMapper<IssueFilter> translator = this.filterTranslators.get(filter.getClass());
    //             if(translator == null){
    //                 throw new IllegalStateException("Mapper for type "+filter.getClass()+" was not registered");
    //             }
    //             List<String> newFragments = translator.filterFragmentsFrom(filter);
    //             List<QueryBuilder> newQueries = new ArrayList<>();
    //             for(String newFragment: newFragments){
    //                 for(QueryBuilder currentBuilder: queries){
    //                     QueryBuilder newBuilder = currentBuilder.copy();
    //                     newBuilder.addFragment(newFragment);
    //                     newQueries.add(newBuilder);
    //                 }
    //             }
    //            queries = newQueries;
    //        }
    //        return queries.stream().map(QueryBuilder::assemble).collect(toList());
    //    }
    //
    //    private static class QueryBuilder {
    //        private List<String> fragments = new ArrayList<>();
    //
    //        public void addFragment(final String fragment) {
    //            this.fragments.add(fragment);
    //        }
    //
    //        public String assemble() {
    //            return this.fragments.stream().collect(joining("&"));
    //        }
    //    }
}
