package com.example.service;


import com.example.entity.Classification;

import java.util.List;

public interface ClassificationService {
    List<Classification> getClassificationList(String keyword);
    void createClassification(Classification Classification);
    void deleteClassification(String id);

    void updateClassification(Classification Classification);
}
