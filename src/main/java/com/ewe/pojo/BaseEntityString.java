package com.ewe.pojo;

import java.util.Arrays;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

@MappedSuperclass
public abstract class BaseEntityString implements Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", unique = true)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return String.format("%s(id=%d)", this.getClass().getSimpleName(), this.getId());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;

		if (o instanceof BaseEntity) {
			final BaseEntity other = (BaseEntity) o;
			return equal(getId(), other.getId());
			// return getId() == other.getId() || (getId() != null &&
			// getId().equals(other.getId()));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getId());
	}

	public static boolean equal(Object a, Object b) {
		return a == b || (a != null && a.equals(b));
	}

	public static int hashCode(Object... objects) {
		return Arrays.hashCode(objects);
	}
}
