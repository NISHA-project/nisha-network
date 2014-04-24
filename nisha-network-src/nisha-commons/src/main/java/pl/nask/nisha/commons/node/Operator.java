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
package pl.nask.nisha.commons.node;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.security.SaltedPasswordEncryptor;

public class Operator implements Serializable {

    private static final long serialVersionUID = -3245666329780958941L;

    private String _id;
    private String _rev;

    private String operatorId;
    private String passwordHash;
    private String salt;
    private String privateKey;

    private String contextNodeName;
    private boolean isBlocked = false;
    private String fullName;
    private String email;
    private String telephone;
    private String certificate;

    static final String HAS_PASSWORD = "***has password***";
    static final String HAS_PASSWORD_NULL = "has null password";


    public static final Logger LOG = LoggerFactory.getLogger(Operator.class);
    public Operator() {
    }

    public Operator(String operatorId, String passwordHash, String privateKey, String contextNodeName, boolean isBlocked, String fullName, String email, String telephone, String certificate) {
        setOperatorId(operatorId);
        this.passwordHash = passwordHash;
        this.privateKey = privateKey;
        this.contextNodeName = contextNodeName;
        this.isBlocked = isBlocked;
        this.fullName = fullName;
        setEmail(email);
        this.telephone = telephone;
        this.certificate = certificate;
    }

    public Operator(String _id, String _rev, String operatorId, String passwordHash, String privateKey, String contextNodeName, boolean isBlocked, String fullName, String email, String telephone, String certificate) {
        this._id = _id;
        this._rev = _rev;
        setOperatorId(operatorId);
        this.passwordHash = passwordHash;
        this.privateKey = privateKey;
        this.contextNodeName = contextNodeName;
        this.isBlocked = isBlocked;
        this.fullName = fullName;
        setEmail(email);
        setTelephone(telephone);
        this.certificate = certificate;
    }

    public Operator(Operator other) {
        this._id = other.get_id();
        this._rev = other.get_rev();
        setOperatorId(other.getOperatorId());
        this.passwordHash = other.getPasswordHash();
        this.privateKey = other.getPrivateKey();
        this.contextNodeName = other.getContextNodeName();
        setBlocked(other.getBlocked());
        this.fullName = other.getFullName();
        setEmail(other.getEmail());
        setTelephone(other.getTelephone());
        this.certificate = other.getCertificate();
    }

    public void hashAndSavePassword(String operatorPass){
        passwordHash = SaltedPasswordEncryptor.getEncryptedPassword(operatorPass, getSalt());
    }

    public String get_id() {
        return _id;
    }

    public String get_rev() {
        return _rev;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        if(operatorId == null || operatorId.trim().isEmpty()) {
            throw new IllegalArgumentException("operator id removal - not allowed");
        }
        this.operatorId = operatorId;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getContextNodeName() {
        return contextNodeName;
    }

    public void setContextNodeName(String contextNodeName) {
        this.contextNodeName = contextNodeName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        try{
            new InternetAddress(email, true);
            this.email = email;
        } catch (AddressException e) {
            throw new IllegalArgumentException(email + " - email is required to follow RFC822 - failure");
        }
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public boolean getBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public String getSalt() {
        if (this.salt == null) {
            try {
                this.salt = SaltedPasswordEncryptor.generateSalt();
                LOG.debug("salt generated for " + this.operatorId);
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e.getMessage() + " - salt cannot be generate");
            }
        }
        return this.salt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Operator)) return false;

        Operator operator = (Operator) o;

        if (isBlocked != operator.isBlocked) return false;
        if (certificate != null ? !certificate.equals(operator.certificate) : operator.certificate != null)
            return false;
        if (contextNodeName != null ? !contextNodeName.equals(operator.contextNodeName) : operator.contextNodeName != null)
            return false;
        if (email != null ? !email.equals(operator.email) : operator.email != null) return false;
        if (fullName != null ? !fullName.equals(operator.fullName) : operator.fullName != null) return false;
        if (operatorId != null ? !operatorId.equals(operator.operatorId) : operator.operatorId != null) return false;
        if (passwordHash != null ? !passwordHash.equals(operator.passwordHash) : operator.passwordHash != null)
            return false;
        if (privateKey != null ? !privateKey.equals(operator.privateKey) : operator.privateKey != null) return false;
        if (telephone != null ? !telephone.equals(operator.telephone) : operator.telephone != null) return false;

        return true;
    }



    @Override
    public int hashCode() {
        int result = operatorId != null ? operatorId.hashCode() : 0;
        result = 31 * result + (passwordHash != null ? passwordHash.hashCode() : 0);
        result = 31 * result + (privateKey != null ? privateKey.hashCode() : 0);
        result = 31 * result + (contextNodeName != null ? contextNodeName.hashCode() : 0);
        result = 31 * result + (isBlocked ? 1 : 0);
        result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (telephone != null ? telephone.hashCode() : 0);
        result = 31 * result + (certificate != null ? certificate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Operator{" +
                "operatorId='" + operatorId + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", contextNodeName='" + contextNodeName + '\'' +
                ", isBlocked=" + isBlocked +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", certificate='" + certificate + '\'' +
                '}';
    }

    public String toStringVerbose() {
        return "Operator{" +
                "_id='" + _id + '\'' +
                ", _rev='" + _rev + '\'' +
                ", operatorId='" + operatorId + '\'' +
                ", passwordHash=" + getHasPasswordString() +
                ", privateKey='***" + '\'' +
                ", contextNodeName='" + contextNodeName + '\'' +
                ", isBlocked='" + isBlocked + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", certificate='" + certificate + '\'' +
                '}';
    }

    String getHasPasswordString () {
        String result;
        if(passwordHash == null) result = HAS_PASSWORD_NULL;
        else {
            result = HAS_PASSWORD;
        }
        return result;
    }

}

