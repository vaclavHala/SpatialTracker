package cz.muni.fi.pv243.spatialtracker.issues.redmine;

import cz.muni.fi.pv243.spatialtracker.issues.redmine.filter.RedmineFilterComposer;
import cz.muni.fi.pv243.spatialtracker.SpatialTracker;
import cz.muni.fi.pv243.spatialtracker.config.Config;
import cz.muni.fi.pv243.spatialtracker.config.Property;
import static cz.muni.fi.pv243.spatialtracker.config.PropertyType.INTEGRATION_PROJECT_ID;
import cz.muni.fi.pv243.spatialtracker.issues.IssueCategory;
import static cz.muni.fi.pv243.spatialtracker.issues.IssueCategory.ADD;
import cz.muni.fi.pv243.spatialtracker.issues.IssuePriority;
import cz.muni.fi.pv243.spatialtracker.issues.IssueStatus;
import static cz.muni.fi.pv243.spatialtracker.issues.IssueStatus.REJECTED;
import cz.muni.fi.pv243.spatialtracker.issues.dto.Coordinates;
import cz.muni.fi.pv243.spatialtracker.issues.filter.CategoryFilter;
import cz.muni.fi.pv243.spatialtracker.issues.filter.IssueFilter;
import cz.muni.fi.pv243.spatialtracker.issues.filter.StatusFilter;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;

@Slf4j
@RunWith(Arquillian.class)
public class RedmineFilterComposerTest {

    @Deployment
    public static WebArchive create() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, RedmineFilterComposerTest.class.getSimpleName() + ".war");
        war.addClasses(
                       IssueCategory.class, RedmineCategoryMapper.class,
                       IssuePriority.class, RedminePriorityMapper.class,
                       IssueStatus.class, RedmineStatusMapper.class)
           .addPackages(false,
                        RedmineFilterComposer.class.getPackage(),
                        IssueFilter.class.getPackage(),
                        Config.class.getPackage(),
                        SpatialTracker.class.getPackage())
           .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
           //ids to map ids in filter are read from here
           .addAsResource("redmine.json");

        return war;
    }

    @Inject
    private RedmineFilterComposer sut;

    @Inject
    private RedmineCategoryMapper catMapper;

    @Inject
    private RedmineStatusMapper statMapper;

    @Test
    public void shouldJoinAllFilterFragmentsUsingAnd() throws Exception {
        IssueCategory category = ADD;
        IssueStatus status = REJECTED;
        String query = this.sut.assembleFilters(asList(new CategoryFilter(category),
                                                       new StatusFilter(status)));
        String expectedQuery = format("category_id=%d&status_id=%d",
                                      this.catMapper.toId(category),
                                      this.statMapper.toId(status));
        assertEquals(expectedQuery, query);
    }
}
