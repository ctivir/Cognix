/*
 * /*******************************************************************************
 *  * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available either under the terms of the GNU Public License v3
 *  * which accompanies this distribution, and is available at
 *  * http://www.gnu.org/licenses/gpl.html or for any other uses contact 
 *  * contato@cognitivabrasil.com.br for information.
 *  ******************************************************************************/

package com.cognitivabrasil.repositorio.web;

import cognitivabrasil.obaa.Educational.TypicalAgeRange;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author luiz
 */
public class ObaaDto {

    //General
    private String title;
    private String language;
    private String aggregationLevel;
    private String structure;
    //Lifecycle
    private String author;
    private String data;
    //Educational
    private String interactivityType;
    private String interactivityLevel;
    private String perception;
    private String copresense;
    private String reciprocity;
    private String tipicalLearningTime;
    private String eduLanguage;
    private String synchronism;
    //Accessibility
    private String visual;
    private String auditory;
    private String textual;
    private String tactil;
    //Technical
    private String size;
    private String format;
    private String requirementsType;
    private String requirementsName;
    private String requirementsMinimumVersion;
    private String installationRemarks;
    private String duration;
    private String otherPlatformRequirements;
    private List<String> supportedPlatforms;

    public ObaaDto() {
        //General
        title = "";
        language = "";
        aggregationLevel = "";
        structure = "";
        
        //Lifecycle
        author = "";
        data = "";
        
        //Educational
        interactivityType = "";
        interactivityLevel = "";
        perception = "";
        copresense = "";
        reciprocity = "";
        tipicalLearningTime = "";
        eduLanguage = "";
        synchronism = "";
        
        //Accessibility
        visual = "";
        auditory = "";
        textual = "";
        tactil = "";
        
        //Technical
        size = "";
        format = "";
        requirementsType = "";
        requirementsName = "";
        requirementsMinimumVersion = "";
        installationRemarks = "";
        duration = "";
        otherPlatformRequirements = "";
        supportedPlatforms = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAggregationLevel() {
        return aggregationLevel;
    }

    public void setAggregationLevel(String aggregationLevel) {
        this.aggregationLevel = aggregationLevel;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getInteractivityType() {
        return interactivityType;
    }

    public void setInteractivityType(String interactivityType) {
        this.interactivityType = interactivityType;
    }

    public String getInteractivityLevel() {
        return interactivityLevel;
    }

    public void setInteractivityLevel(String interactivityLevel) {
        this.interactivityLevel = interactivityLevel;
    }

    public String getPerception() {
        return perception;
    }

    public void setPerception(String perception) {
        this.perception = perception;
    }

    public String getCopresense() {
        return copresense;
    }

    public void setCopresense(String copresense) {
        this.copresense = copresense;
    }

    public String getReciprocity() {
        return reciprocity;
    }

    public void setReciprocity(String reciprocity) {
        this.reciprocity = reciprocity;
    }

    public String getTipicalLearningTime() {
        return tipicalLearningTime;
    }

    public void setTipicalLearningTime(String tipicalLearningTime) {
        this.tipicalLearningTime = tipicalLearningTime;
    }

    public String getEduLanguage() {
        return eduLanguage;
    }

    public void setEduLanguage(String eduLanguage) {
        this.eduLanguage = eduLanguage;
    }

    public String getVisual() {
        return visual;
    }

    public void setVisual(String visual) {
        this.visual = visual;
    }

    public String getAuditory() {
        return auditory;
    }

    public void setAuditory(String auditory) {
        this.auditory = auditory;
    }

    public String getTextual() {
        return textual;
    }

    public void setTextual(String textual) {
        this.textual = textual;
    }

    public String getTactil() {
        return tactil;
    }

    public void setTactil(String tactil) {
        this.tactil = tactil;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getRequirementsType() {
        return requirementsType;
    }

    public void setRequirementsType(String requirementsType) {
        this.requirementsType = requirementsType;
    }

    public String getRequirementsName() {
        return requirementsName;
    }

    public void setRequirementsName(String requirementsName) {
        this.requirementsName = requirementsName;
    }

    public String getRequirementsMinimumVersion() {
        return requirementsMinimumVersion;
    }

    public void setRequirementsMinimumVersion(String requirementsMinimumVersion) {
        this.requirementsMinimumVersion = requirementsMinimumVersion;
    }

    public String getInstallationRemarks() {
        return installationRemarks;
    }

    public void setInstallationRemarks(String installationRemarks) {
        this.installationRemarks = installationRemarks;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List getSupportedPlatforms() {
        return supportedPlatforms;
    }

    public void addSupportedPlatforms(String supportedPlatform) {
        this.supportedPlatforms.add(supportedPlatform);
    }

    public String getSynchronism() {
        return synchronism;
    }

    public void setSynchronism(String synchronism) {
        this.synchronism = synchronism;
    }

    public String getOtherPlatformRequirements() {
        return otherPlatformRequirements;
    }

    public void setOtherPlatformRequirements(String otherPlatformRequirements) {
        this.otherPlatformRequirements = otherPlatformRequirements;
    }
}
