package com.example.service;


import com.example.entity.ErrorLog;

import java.util.List;

public interface ErrorLogService {
    List<ErrorLog> getErrorLogList();
    void createErrorLog(ErrorLog errorLog);
    void deleteErrorLog(String id);
    void updateErrorLog(ErrorLog errorLog);
}
