package com.agfa.he.sh.cris.domain;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Embeddable
public class EmbeddableBaseData implements Serializable {

	private static final long serialVersionUID = 5763814158426686389L;

	public static final EmbeddableBaseData clone(EmbeddableBaseData source) {
		if (source != null) {
			EmbeddableBaseData embed = new EmbeddableBaseData();
			embed.setId(source.getId());
			embed.setBizId(source.getBizId());
			embed.setName(source.getName());
			embed.setCategory(source.getCategory());
			return embed;
		} else {
			return null;
		}
	}

	private String bizId;
	private String category;
	private String id;
	private String name;
	

	@Override
	public boolean equals(Object o) {
		if ((o == null) || !(o instanceof EmbeddableBaseData)) {
			return false;
		}
		EmbeddableBaseData od = (EmbeddableBaseData) o;
		return new EqualsBuilder().append(this.id, od.id).append(this.bizId, od.bizId).append(this.name, od.name).append(
				this.category, od.category).isEquals();
	}

	
	public String getBizId() {
		return bizId;
	}

	public String getCategory() {
		return category;
	}

	public String getId() {
		return id;
	}

	
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(61, 15).append(id).append(bizId).append(name).append(category).toHashCode();

	}

	public void setBizId(String bizId) {
		this.bizId = bizId;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toDisplayString() {
		StringBuffer sb = new StringBuffer();
		sb.append(bizId);
		sb.append(",");
		sb.append(name);
		return sb.toString();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id).append("bizId", bizId).append("name", name).append(
				"category", category).toString();

	}
}