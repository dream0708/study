/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.sohu.bp.elite.model;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TSearchGlobalListResult implements org.apache.thrift.TBase<TSearchGlobalListResult, TSearchGlobalListResult._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TSearchGlobalListResult");

  private static final org.apache.thrift.protocol.TField QUESTIONS_FIELD_DESC = new org.apache.thrift.protocol.TField("questions", org.apache.thrift.protocol.TType.LIST, (short)1);
  private static final org.apache.thrift.protocol.TField ANSWERS_FIELD_DESC = new org.apache.thrift.protocol.TField("answers", org.apache.thrift.protocol.TType.LIST, (short)2);
  private static final org.apache.thrift.protocol.TField USERS_FIELD_DESC = new org.apache.thrift.protocol.TField("users", org.apache.thrift.protocol.TType.LIST, (short)3);
  private static final org.apache.thrift.protocol.TField RANK_FIELD_DESC = new org.apache.thrift.protocol.TField("rank", org.apache.thrift.protocol.TType.LIST, (short)5);
  private static final org.apache.thrift.protocol.TField TOTAL_COUNTS_FIELD_DESC = new org.apache.thrift.protocol.TField("totalCounts", org.apache.thrift.protocol.TType.MAP, (short)10);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TSearchGlobalListResultStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TSearchGlobalListResultTupleSchemeFactory());
  }

  public List<TEliteQuestion> questions; // required
  public List<TEliteAnswer> answers; // required
  public List<TEliteUser> users; // required
  public List<String> rank; // required
  public Map<String,Integer> totalCounts; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    QUESTIONS((short)1, "questions"),
    ANSWERS((short)2, "answers"),
    USERS((short)3, "users"),
    RANK((short)5, "rank"),
    TOTAL_COUNTS((short)10, "totalCounts");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // QUESTIONS
          return QUESTIONS;
        case 2: // ANSWERS
          return ANSWERS;
        case 3: // USERS
          return USERS;
        case 5: // RANK
          return RANK;
        case 10: // TOTAL_COUNTS
          return TOTAL_COUNTS;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.QUESTIONS, new org.apache.thrift.meta_data.FieldMetaData("questions", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TEliteQuestion.class))));
    tmpMap.put(_Fields.ANSWERS, new org.apache.thrift.meta_data.FieldMetaData("answers", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TEliteAnswer.class))));
    tmpMap.put(_Fields.USERS, new org.apache.thrift.meta_data.FieldMetaData("users", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TEliteUser.class))));
    tmpMap.put(_Fields.RANK, new org.apache.thrift.meta_data.FieldMetaData("rank", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING))));
    tmpMap.put(_Fields.TOTAL_COUNTS, new org.apache.thrift.meta_data.FieldMetaData("totalCounts", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING), 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TSearchGlobalListResult.class, metaDataMap);
  }

  public TSearchGlobalListResult() {
  }

  public TSearchGlobalListResult(
    List<TEliteQuestion> questions,
    List<TEliteAnswer> answers,
    List<TEliteUser> users,
    List<String> rank,
    Map<String,Integer> totalCounts)
  {
    this();
    this.questions = questions;
    this.answers = answers;
    this.users = users;
    this.rank = rank;
    this.totalCounts = totalCounts;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TSearchGlobalListResult(TSearchGlobalListResult other) {
    if (other.isSetQuestions()) {
      List<TEliteQuestion> __this__questions = new ArrayList<TEliteQuestion>();
      for (TEliteQuestion other_element : other.questions) {
        __this__questions.add(new TEliteQuestion(other_element));
      }
      this.questions = __this__questions;
    }
    if (other.isSetAnswers()) {
      List<TEliteAnswer> __this__answers = new ArrayList<TEliteAnswer>();
      for (TEliteAnswer other_element : other.answers) {
        __this__answers.add(new TEliteAnswer(other_element));
      }
      this.answers = __this__answers;
    }
    if (other.isSetUsers()) {
      List<TEliteUser> __this__users = new ArrayList<TEliteUser>();
      for (TEliteUser other_element : other.users) {
        __this__users.add(new TEliteUser(other_element));
      }
      this.users = __this__users;
    }
    if (other.isSetRank()) {
      List<String> __this__rank = new ArrayList<String>();
      for (String other_element : other.rank) {
        __this__rank.add(other_element);
      }
      this.rank = __this__rank;
    }
    if (other.isSetTotalCounts()) {
      Map<String,Integer> __this__totalCounts = new HashMap<String,Integer>();
      for (Map.Entry<String, Integer> other_element : other.totalCounts.entrySet()) {

        String other_element_key = other_element.getKey();
        Integer other_element_value = other_element.getValue();

        String __this__totalCounts_copy_key = other_element_key;

        Integer __this__totalCounts_copy_value = other_element_value;

        __this__totalCounts.put(__this__totalCounts_copy_key, __this__totalCounts_copy_value);
      }
      this.totalCounts = __this__totalCounts;
    }
  }

  public TSearchGlobalListResult deepCopy() {
    return new TSearchGlobalListResult(this);
  }

  @Override
  public void clear() {
    this.questions = null;
    this.answers = null;
    this.users = null;
    this.rank = null;
    this.totalCounts = null;
  }

  public int getQuestionsSize() {
    return (this.questions == null) ? 0 : this.questions.size();
  }

  public java.util.Iterator<TEliteQuestion> getQuestionsIterator() {
    return (this.questions == null) ? null : this.questions.iterator();
  }

  public void addToQuestions(TEliteQuestion elem) {
    if (this.questions == null) {
      this.questions = new ArrayList<TEliteQuestion>();
    }
    this.questions.add(elem);
  }

  public List<TEliteQuestion> getQuestions() {
    return this.questions;
  }

  public TSearchGlobalListResult setQuestions(List<TEliteQuestion> questions) {
    this.questions = questions;
    return this;
  }

  public void unsetQuestions() {
    this.questions = null;
  }

  /** Returns true if field questions is set (has been assigned a value) and false otherwise */
  public boolean isSetQuestions() {
    return this.questions != null;
  }

  public void setQuestionsIsSet(boolean value) {
    if (!value) {
      this.questions = null;
    }
  }

  public int getAnswersSize() {
    return (this.answers == null) ? 0 : this.answers.size();
  }

  public java.util.Iterator<TEliteAnswer> getAnswersIterator() {
    return (this.answers == null) ? null : this.answers.iterator();
  }

  public void addToAnswers(TEliteAnswer elem) {
    if (this.answers == null) {
      this.answers = new ArrayList<TEliteAnswer>();
    }
    this.answers.add(elem);
  }

  public List<TEliteAnswer> getAnswers() {
    return this.answers;
  }

  public TSearchGlobalListResult setAnswers(List<TEliteAnswer> answers) {
    this.answers = answers;
    return this;
  }

  public void unsetAnswers() {
    this.answers = null;
  }

  /** Returns true if field answers is set (has been assigned a value) and false otherwise */
  public boolean isSetAnswers() {
    return this.answers != null;
  }

  public void setAnswersIsSet(boolean value) {
    if (!value) {
      this.answers = null;
    }
  }

  public int getUsersSize() {
    return (this.users == null) ? 0 : this.users.size();
  }

  public java.util.Iterator<TEliteUser> getUsersIterator() {
    return (this.users == null) ? null : this.users.iterator();
  }

  public void addToUsers(TEliteUser elem) {
    if (this.users == null) {
      this.users = new ArrayList<TEliteUser>();
    }
    this.users.add(elem);
  }

  public List<TEliteUser> getUsers() {
    return this.users;
  }

  public TSearchGlobalListResult setUsers(List<TEliteUser> users) {
    this.users = users;
    return this;
  }

  public void unsetUsers() {
    this.users = null;
  }

  /** Returns true if field users is set (has been assigned a value) and false otherwise */
  public boolean isSetUsers() {
    return this.users != null;
  }

  public void setUsersIsSet(boolean value) {
    if (!value) {
      this.users = null;
    }
  }

  public int getRankSize() {
    return (this.rank == null) ? 0 : this.rank.size();
  }

  public java.util.Iterator<String> getRankIterator() {
    return (this.rank == null) ? null : this.rank.iterator();
  }

  public void addToRank(String elem) {
    if (this.rank == null) {
      this.rank = new ArrayList<String>();
    }
    this.rank.add(elem);
  }

  public List<String> getRank() {
    return this.rank;
  }

  public TSearchGlobalListResult setRank(List<String> rank) {
    this.rank = rank;
    return this;
  }

  public void unsetRank() {
    this.rank = null;
  }

  /** Returns true if field rank is set (has been assigned a value) and false otherwise */
  public boolean isSetRank() {
    return this.rank != null;
  }

  public void setRankIsSet(boolean value) {
    if (!value) {
      this.rank = null;
    }
  }

  public int getTotalCountsSize() {
    return (this.totalCounts == null) ? 0 : this.totalCounts.size();
  }

  public void putToTotalCounts(String key, int val) {
    if (this.totalCounts == null) {
      this.totalCounts = new HashMap<String,Integer>();
    }
    this.totalCounts.put(key, val);
  }

  public Map<String,Integer> getTotalCounts() {
    return this.totalCounts;
  }

  public TSearchGlobalListResult setTotalCounts(Map<String,Integer> totalCounts) {
    this.totalCounts = totalCounts;
    return this;
  }

  public void unsetTotalCounts() {
    this.totalCounts = null;
  }

  /** Returns true if field totalCounts is set (has been assigned a value) and false otherwise */
  public boolean isSetTotalCounts() {
    return this.totalCounts != null;
  }

  public void setTotalCountsIsSet(boolean value) {
    if (!value) {
      this.totalCounts = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case QUESTIONS:
      if (value == null) {
        unsetQuestions();
      } else {
        setQuestions((List<TEliteQuestion>)value);
      }
      break;

    case ANSWERS:
      if (value == null) {
        unsetAnswers();
      } else {
        setAnswers((List<TEliteAnswer>)value);
      }
      break;

    case USERS:
      if (value == null) {
        unsetUsers();
      } else {
        setUsers((List<TEliteUser>)value);
      }
      break;

    case RANK:
      if (value == null) {
        unsetRank();
      } else {
        setRank((List<String>)value);
      }
      break;

    case TOTAL_COUNTS:
      if (value == null) {
        unsetTotalCounts();
      } else {
        setTotalCounts((Map<String,Integer>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case QUESTIONS:
      return getQuestions();

    case ANSWERS:
      return getAnswers();

    case USERS:
      return getUsers();

    case RANK:
      return getRank();

    case TOTAL_COUNTS:
      return getTotalCounts();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case QUESTIONS:
      return isSetQuestions();
    case ANSWERS:
      return isSetAnswers();
    case USERS:
      return isSetUsers();
    case RANK:
      return isSetRank();
    case TOTAL_COUNTS:
      return isSetTotalCounts();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TSearchGlobalListResult)
      return this.equals((TSearchGlobalListResult)that);
    return false;
  }

  public boolean equals(TSearchGlobalListResult that) {
    if (that == null)
      return false;

    boolean this_present_questions = true && this.isSetQuestions();
    boolean that_present_questions = true && that.isSetQuestions();
    if (this_present_questions || that_present_questions) {
      if (!(this_present_questions && that_present_questions))
        return false;
      if (!this.questions.equals(that.questions))
        return false;
    }

    boolean this_present_answers = true && this.isSetAnswers();
    boolean that_present_answers = true && that.isSetAnswers();
    if (this_present_answers || that_present_answers) {
      if (!(this_present_answers && that_present_answers))
        return false;
      if (!this.answers.equals(that.answers))
        return false;
    }

    boolean this_present_users = true && this.isSetUsers();
    boolean that_present_users = true && that.isSetUsers();
    if (this_present_users || that_present_users) {
      if (!(this_present_users && that_present_users))
        return false;
      if (!this.users.equals(that.users))
        return false;
    }

    boolean this_present_rank = true && this.isSetRank();
    boolean that_present_rank = true && that.isSetRank();
    if (this_present_rank || that_present_rank) {
      if (!(this_present_rank && that_present_rank))
        return false;
      if (!this.rank.equals(that.rank))
        return false;
    }

    boolean this_present_totalCounts = true && this.isSetTotalCounts();
    boolean that_present_totalCounts = true && that.isSetTotalCounts();
    if (this_present_totalCounts || that_present_totalCounts) {
      if (!(this_present_totalCounts && that_present_totalCounts))
        return false;
      if (!this.totalCounts.equals(that.totalCounts))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(TSearchGlobalListResult other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    TSearchGlobalListResult typedOther = (TSearchGlobalListResult)other;

    lastComparison = Boolean.valueOf(isSetQuestions()).compareTo(typedOther.isSetQuestions());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetQuestions()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.questions, typedOther.questions);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetAnswers()).compareTo(typedOther.isSetAnswers());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAnswers()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.answers, typedOther.answers);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetUsers()).compareTo(typedOther.isSetUsers());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUsers()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.users, typedOther.users);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetRank()).compareTo(typedOther.isSetRank());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRank()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.rank, typedOther.rank);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetTotalCounts()).compareTo(typedOther.isSetTotalCounts());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTotalCounts()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.totalCounts, typedOther.totalCounts);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("TSearchGlobalListResult(");
    boolean first = true;

    sb.append("questions:");
    if (this.questions == null) {
      sb.append("null");
    } else {
      sb.append(this.questions);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("answers:");
    if (this.answers == null) {
      sb.append("null");
    } else {
      sb.append(this.answers);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("users:");
    if (this.users == null) {
      sb.append("null");
    } else {
      sb.append(this.users);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("rank:");
    if (this.rank == null) {
      sb.append("null");
    } else {
      sb.append(this.rank);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("totalCounts:");
    if (this.totalCounts == null) {
      sb.append("null");
    } else {
      sb.append(this.totalCounts);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TSearchGlobalListResultStandardSchemeFactory implements SchemeFactory {
    public TSearchGlobalListResultStandardScheme getScheme() {
      return new TSearchGlobalListResultStandardScheme();
    }
  }

  private static class TSearchGlobalListResultStandardScheme extends StandardScheme<TSearchGlobalListResult> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TSearchGlobalListResult struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // QUESTIONS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list58 = iprot.readListBegin();
                struct.questions = new ArrayList<TEliteQuestion>(_list58.size);
                for (int _i59 = 0; _i59 < _list58.size; ++_i59)
                {
                  TEliteQuestion _elem60; // required
                  _elem60 = new TEliteQuestion();
                  _elem60.read(iprot);
                  struct.questions.add(_elem60);
                }
                iprot.readListEnd();
              }
              struct.setQuestionsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // ANSWERS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list61 = iprot.readListBegin();
                struct.answers = new ArrayList<TEliteAnswer>(_list61.size);
                for (int _i62 = 0; _i62 < _list61.size; ++_i62)
                {
                  TEliteAnswer _elem63; // required
                  _elem63 = new TEliteAnswer();
                  _elem63.read(iprot);
                  struct.answers.add(_elem63);
                }
                iprot.readListEnd();
              }
              struct.setAnswersIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // USERS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list64 = iprot.readListBegin();
                struct.users = new ArrayList<TEliteUser>(_list64.size);
                for (int _i65 = 0; _i65 < _list64.size; ++_i65)
                {
                  TEliteUser _elem66; // required
                  _elem66 = new TEliteUser();
                  _elem66.read(iprot);
                  struct.users.add(_elem66);
                }
                iprot.readListEnd();
              }
              struct.setUsersIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // RANK
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list67 = iprot.readListBegin();
                struct.rank = new ArrayList<String>(_list67.size);
                for (int _i68 = 0; _i68 < _list67.size; ++_i68)
                {
                  String _elem69; // required
                  _elem69 = iprot.readString();
                  struct.rank.add(_elem69);
                }
                iprot.readListEnd();
              }
              struct.setRankIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 10: // TOTAL_COUNTS
            if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
              {
                org.apache.thrift.protocol.TMap _map70 = iprot.readMapBegin();
                struct.totalCounts = new HashMap<String,Integer>(2*_map70.size);
                for (int _i71 = 0; _i71 < _map70.size; ++_i71)
                {
                  String _key72; // required
                  int _val73; // required
                  _key72 = iprot.readString();
                  _val73 = iprot.readI32();
                  struct.totalCounts.put(_key72, _val73);
                }
                iprot.readMapEnd();
              }
              struct.setTotalCountsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TSearchGlobalListResult struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.questions != null) {
        oprot.writeFieldBegin(QUESTIONS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.questions.size()));
          for (TEliteQuestion _iter74 : struct.questions)
          {
            _iter74.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.answers != null) {
        oprot.writeFieldBegin(ANSWERS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.answers.size()));
          for (TEliteAnswer _iter75 : struct.answers)
          {
            _iter75.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.users != null) {
        oprot.writeFieldBegin(USERS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.users.size()));
          for (TEliteUser _iter76 : struct.users)
          {
            _iter76.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.rank != null) {
        oprot.writeFieldBegin(RANK_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, struct.rank.size()));
          for (String _iter77 : struct.rank)
          {
            oprot.writeString(_iter77);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.totalCounts != null) {
        oprot.writeFieldBegin(TOTAL_COUNTS_FIELD_DESC);
        {
          oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.I32, struct.totalCounts.size()));
          for (Map.Entry<String, Integer> _iter78 : struct.totalCounts.entrySet())
          {
            oprot.writeString(_iter78.getKey());
            oprot.writeI32(_iter78.getValue());
          }
          oprot.writeMapEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TSearchGlobalListResultTupleSchemeFactory implements SchemeFactory {
    public TSearchGlobalListResultTupleScheme getScheme() {
      return new TSearchGlobalListResultTupleScheme();
    }
  }

  private static class TSearchGlobalListResultTupleScheme extends TupleScheme<TSearchGlobalListResult> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TSearchGlobalListResult struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetQuestions()) {
        optionals.set(0);
      }
      if (struct.isSetAnswers()) {
        optionals.set(1);
      }
      if (struct.isSetUsers()) {
        optionals.set(2);
      }
      if (struct.isSetRank()) {
        optionals.set(3);
      }
      if (struct.isSetTotalCounts()) {
        optionals.set(4);
      }
      oprot.writeBitSet(optionals, 5);
      if (struct.isSetQuestions()) {
        {
          oprot.writeI32(struct.questions.size());
          for (TEliteQuestion _iter79 : struct.questions)
          {
            _iter79.write(oprot);
          }
        }
      }
      if (struct.isSetAnswers()) {
        {
          oprot.writeI32(struct.answers.size());
          for (TEliteAnswer _iter80 : struct.answers)
          {
            _iter80.write(oprot);
          }
        }
      }
      if (struct.isSetUsers()) {
        {
          oprot.writeI32(struct.users.size());
          for (TEliteUser _iter81 : struct.users)
          {
            _iter81.write(oprot);
          }
        }
      }
      if (struct.isSetRank()) {
        {
          oprot.writeI32(struct.rank.size());
          for (String _iter82 : struct.rank)
          {
            oprot.writeString(_iter82);
          }
        }
      }
      if (struct.isSetTotalCounts()) {
        {
          oprot.writeI32(struct.totalCounts.size());
          for (Map.Entry<String, Integer> _iter83 : struct.totalCounts.entrySet())
          {
            oprot.writeString(_iter83.getKey());
            oprot.writeI32(_iter83.getValue());
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TSearchGlobalListResult struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(5);
      if (incoming.get(0)) {
        {
          org.apache.thrift.protocol.TList _list84 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.questions = new ArrayList<TEliteQuestion>(_list84.size);
          for (int _i85 = 0; _i85 < _list84.size; ++_i85)
          {
            TEliteQuestion _elem86; // required
            _elem86 = new TEliteQuestion();
            _elem86.read(iprot);
            struct.questions.add(_elem86);
          }
        }
        struct.setQuestionsIsSet(true);
      }
      if (incoming.get(1)) {
        {
          org.apache.thrift.protocol.TList _list87 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.answers = new ArrayList<TEliteAnswer>(_list87.size);
          for (int _i88 = 0; _i88 < _list87.size; ++_i88)
          {
            TEliteAnswer _elem89; // required
            _elem89 = new TEliteAnswer();
            _elem89.read(iprot);
            struct.answers.add(_elem89);
          }
        }
        struct.setAnswersIsSet(true);
      }
      if (incoming.get(2)) {
        {
          org.apache.thrift.protocol.TList _list90 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.users = new ArrayList<TEliteUser>(_list90.size);
          for (int _i91 = 0; _i91 < _list90.size; ++_i91)
          {
            TEliteUser _elem92; // required
            _elem92 = new TEliteUser();
            _elem92.read(iprot);
            struct.users.add(_elem92);
          }
        }
        struct.setUsersIsSet(true);
      }
      if (incoming.get(3)) {
        {
          org.apache.thrift.protocol.TList _list93 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, iprot.readI32());
          struct.rank = new ArrayList<String>(_list93.size);
          for (int _i94 = 0; _i94 < _list93.size; ++_i94)
          {
            String _elem95; // required
            _elem95 = iprot.readString();
            struct.rank.add(_elem95);
          }
        }
        struct.setRankIsSet(true);
      }
      if (incoming.get(4)) {
        {
          org.apache.thrift.protocol.TMap _map96 = new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.I32, iprot.readI32());
          struct.totalCounts = new HashMap<String,Integer>(2*_map96.size);
          for (int _i97 = 0; _i97 < _map96.size; ++_i97)
          {
            String _key98; // required
            int _val99; // required
            _key98 = iprot.readString();
            _val99 = iprot.readI32();
            struct.totalCounts.put(_key98, _val99);
          }
        }
        struct.setTotalCountsIsSet(true);
      }
    }
  }

}
