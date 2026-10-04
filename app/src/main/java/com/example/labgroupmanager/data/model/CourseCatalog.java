package com.example.labgroupmanager.data.model;

public class CourseCatalog {

    public static String getCoursesForProgramme(String programme) {
        if (programme == null) return "ICT361 Mobile App Development";

        switch (programme.toUpperCase().trim()) {
            case "CS":
            case "COMPUTER SCIENCE":
                return "ICT361 - Mobile App Development, CS311 - Data Structures, CS321 - Software Engineering, CS331 - Database Systems";
            case "IT":
            case "INFORMATION TECHNOLOGY":
                return "ICT361 - Mobile App Development, IT311 - Web Technologies, IT321 - Information Systems Security, IT331 - IT Project Management";
            case "DS":
            case "DATA SCIENCE":
                return "ICT361 - Mobile App Development, DS311 - Principles of Data Science, DS321 - Machine Learning, DS331 - Data Mining";
            default:
                return "ICT361 - Mobile App Development, BMG - Principles of Management, Cyber Security - Security Principles";
        }
    }
}
