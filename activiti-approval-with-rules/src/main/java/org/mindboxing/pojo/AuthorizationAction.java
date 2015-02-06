package org.mindboxing.pojo;

import java.io.Serializable;

import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class AuthorizationAction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean managerApproverRequired = true;
	private boolean autoApproved = false;
	private float fareOption, saving;
	private String decision;
	
	public boolean isManagerApproverRequired() {
		return managerApproverRequired;
	}
	public void setManagerApproverRequired(boolean managerApproverRequired) {
		this.managerApproverRequired = managerApproverRequired;
	}
	public boolean isAutoApproved() {
		return autoApproved;
	}
	public void setAutoApproved(boolean autoApproved) {
		this.autoApproved = autoApproved;
	}
	public float getFareOption() {
		return fareOption;
	}
	public void setFareOption(float fareOption) {
		this.fareOption = fareOption;
	}
	
	
	public String getDecision() {
		return decision;
	}
	public void setDecision(String decision) {
		this.decision = decision;
	}
	public float getSaving() {
		return saving;
	}
	public void setSaving(float saving) {
		this.saving = saving;
	}
	
	
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + (autoApproved ? 1231 : 1237);
//		result = prime * result + Float.floatToIntBits(fareOption);
//		result = prime * result + (managerApproverRequired ? 1231 : 1237);
//		return result;
//	}
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		AuthorizationAction other = (AuthorizationAction) obj;
//		if (autoApproved != other.autoApproved)
//			return false;
//		if (Float.floatToIntBits(fareOption) != Float
//				.floatToIntBits(other.fareOption))
//			return false;
//		if (managerApproverRequired != other.managerApproverRequired)
//			return false;
//		return true;
//	}
	
}
