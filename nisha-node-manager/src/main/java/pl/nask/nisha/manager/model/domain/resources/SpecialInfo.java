/*******************************************************************************
 * Copyright (c) 2012 Research and Academic Computer Network.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * With financial support from the Prevention, Preparedness and Consequence
 * Management of Terrorism and other Security Related Risks Programme
 * European Commission - Directorate-General Home Affairs
 *
 * Contributors:
 *     Research and Academic Computer Network
 ******************************************************************************/
package pl.nask.nisha.manager.model.domain.resources;

import java.util.Collection;

public class SpecialInfo {

    private String damageDescription;
    private String vulnerabilityDescription;
    private String consequencesDescription;
    private String possibleSolution;
    private String CVEID;
    private String CVEDate;
    private String propagationMethod;
    private String CVEAuthor;
    private String exploitationProbability;
    private String exploitationDamageLevel;

    private Collection<VulnerableSoftware> vulnerableSoftware;


    public String getDamageDescription() {
        return damageDescription;
    }

    public void setDamageDescription(String damageDescription) {
        this.damageDescription = damageDescription;
    }

    public String getVulnerabilityDescription() {
        return vulnerabilityDescription;
    }

    public void setVulnerabilityDescription(String vulnerabilityDescription) {
        this.vulnerabilityDescription = vulnerabilityDescription;
    }

    public String getConsequencesDescription() {
        return consequencesDescription;
    }

    public void setConsequencesDescription(String consequencesDescription) {
        this.consequencesDescription = consequencesDescription;
    }

    public String getPossibleSolution() {
        return possibleSolution;
    }

    public void setPossibleSolution(String possibleSolution) {
        this.possibleSolution = possibleSolution;
    }

    public String getCVEID() {
        return CVEID;
    }

    public void setCVEID(String CVEID) {
        this.CVEID = CVEID;
    }

    public String getCVEDate() {
        return CVEDate;
    }

    public void setCVEDate(String CVEDate) {
        this.CVEDate = CVEDate;
    }

    public String getPropagationMethod() {
        return propagationMethod;
    }

    public void setPropagationMethod(String propagationMethod) {
        this.propagationMethod = propagationMethod;
    }

    public String getCVEAuthor() {
        return CVEAuthor;
    }

    public void setCVEAuthor(String CVEAuthor) {
        this.CVEAuthor = CVEAuthor;
    }

    public String getExploitationProbability() {
        return exploitationProbability;
    }

    public void setExploitationProbability(String exploitationProbability) {
        this.exploitationProbability = exploitationProbability;
    }

    public String getExploitationDamageLevel() {
        return exploitationDamageLevel;
    }

    public void setExploitationDamageLevel(String exploitationDamageLevel) {
        this.exploitationDamageLevel = exploitationDamageLevel;
    }

    public Collection<VulnerableSoftware> getVulnerableSoftware() {
        return vulnerableSoftware;
    }

    public void setVulnerableSoftware(Collection<VulnerableSoftware> vulnerableSoftware) {
        this.vulnerableSoftware = vulnerableSoftware;
    }

    @Override
    public String toString() {
        return "SpecialInfo{" +
                "damageDescription='" + damageDescription + '\'' +
                ", vulnerabilityDescription='" + vulnerabilityDescription + '\'' +
                ", consequencesDescription='" + consequencesDescription + '\'' +
                ", possibleSolution='" + possibleSolution + '\'' +
                ", CVEID='" + CVEID + '\'' +
                ", CVEDate='" + CVEDate + '\'' +
                ", propagationMethod='" + propagationMethod + '\'' +
                ", CVEAuthor='" + CVEAuthor + '\'' +
                ", exploitationProbability='" + exploitationProbability + '\'' +
                ", exploitationDamageLevel='" + exploitationDamageLevel + '\'' +
                ", vulnerableSoftware=" + vulnerableSoftware +
                '}';
    }
}

