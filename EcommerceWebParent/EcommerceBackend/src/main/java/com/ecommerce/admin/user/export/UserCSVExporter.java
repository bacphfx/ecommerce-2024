package com.ecommerce.admin.user.export;

import com.ecommerce.admin.AbstractExporter;
import com.ecommerce.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class UserCSVExporter extends AbstractExporter {
    public void export(List<User> userList, HttpServletResponse response) throws IOException {
        super.setHeaderResponse(response, "text/csv", ".csv", "users_");
        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"User ID", "Email", "First Name", "Last Name", "Roles", "Enabled"};
        String[] fileMapping = {"id", "email", "firstName", "lastName", "roles", "enabled"};

        csvBeanWriter.writeHeader(csvHeader);

        for (User user : userList){
            csvBeanWriter.write(user, fileMapping);
        }

            csvBeanWriter.close();
    }
}
