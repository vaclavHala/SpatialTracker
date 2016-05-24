package cz.muni.fi.pv243.spatialtracker.issues.redmine.filter;

import cz.muni.fi.pv243.spatialtracker.issues.filter.CategoryFilter;
import cz.muni.fi.pv243.spatialtracker.issues.redmine.RedmineCategoryMapper;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import javax.inject.Inject;

public class CategoryFilterMapper implements RedmineFilterMapper<CategoryFilter> {

    @Inject
    private RedmineCategoryMapper mapper;

    @Override
    public Class<CategoryFilter> targetClass() {
        return CategoryFilter.class;
    }

    @Override
    public String filterFragmentFrom(final CategoryFilter declaration) {
        String formattedCats = declaration.interestCategories().stream()
                                          .map(cat -> String.valueOf(this.mapper.toId(cat)))
                                          .collect(joining("|"));
        return format("category_id=" + formattedCats);
    }
}
