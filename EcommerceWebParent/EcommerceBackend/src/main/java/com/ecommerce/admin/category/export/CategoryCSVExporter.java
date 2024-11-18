package com.ecommerce.admin.category.export;

import com.ecommerce.admin.AbstractExporter;
import com.ecommerce.common.entity.Category;
import com.ecommerce.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class CategoryCSVExporter extends AbstractExporter {
    public void export(List<Category> categoryList, HttpServletResponse response) throws IOException {
        super.setHeaderResponse(response, "text/csv", ".csv", "categories_");
        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"Category ID", "Category Name"};
        String[] fileMapping = {"id", "name"};

        csvBeanWriter.writeHeader(csvHeader);

        for (Category category : categoryList){
            category.setName(category.getName().replace("--", "  "));
            csvBeanWriter.write(category, fileMapping);
        }

            csvBeanWriter.close();
    }
}
