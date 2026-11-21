package org.example.excelinputtodb.ExceltoDatabase;

import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Controller
public class Home {

    @RequestMapping("/")
    public String greet() {
        return "index";
    }

    @PostMapping("/upload")
    public String getFile(@RequestParam("uploaded_file") MultipartFile file, Model model) throws IOException, SQLException {

        if (file.isEmpty()) {
            model.addAttribute("message", "Select a excel File");
            return "index";
        }
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.toLowerCase().endsWith(".xlsx")) && (!filename.toLowerCase().endsWith(".xls"))) {
            model.addAttribute("message", "only excel files are allowed");
            return "index";
        }

                InputStream fs = file.getInputStream();
                Workbook workbook = WorkbookFactory.create(fs);

            Sheet sheet = workbook.getSheetAt(0);

            String query = "INSERT INTO data(id, name, salary) VALUES(?,?,?)";
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DB_NAME", "YOUR_USERNAME", "YOUR_PASSWORD");
            PreparedStatement ps = con.prepareStatement(query);

                DataFormatter data = new DataFormatter();
                int insertcount = 0;

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                    Row row = sheet.getRow(i);

                    if (row == null) {
                        continue;
                    }

                    String id = data.formatCellValue(row.getCell(0));
                    String name = data.formatCellValue(row.getCell(1));
                    String salary = data.formatCellValue(row.getCell(2));


                    if (id.isBlank() || name.isBlank() || salary.isBlank()) {
                        continue;
                    }

                    ps.setInt(1, Integer.parseInt(id));
                    ps.setString(2, name);
                    ps.setInt(3, Integer.parseInt(salary));

                    ps.executeUpdate();
                    insertcount++;
                }
                model.addAttribute("message", " values inserted into the excel file");
                return "index";
        }
    }
