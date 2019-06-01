package com.android.hutils;

import android.content.ContentValues;

import com.android.vcard.VCardBuilder;
import com.android.vcard.VCardConfig;

import java.util.List;

public class VcardFormat {

    private static final int MAX_LENGTH = 2500;
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_NAME_SIZE = 3;
    private static final int MAX_PHONE_LENGTH = 100;
    private static final int MAX_PHONE_SIZE = 3;
    private static final int MAX_EMAIL_LENGTH = 100;
    private static final int MAX_EMAIL_SIZE = 3;

    private static final int NAME = 1;
    private static final int PHONE = 2;
    private static final int EMAIL = 3;
    private static final int NICKNAME = 4;
    private static final int POSTAL = 5;
    private static final int ORGANIZATION = 6;
    private static final int WEBSITE = 7;
    private static final int NOTE = 8;
    private static final int IMS = 9;
    private static final int EVENT = 10;
    private static final int SIPADDRESS = 11;
    private static final int RELATION = 12;

    private static final String DATA1 = "data1";

    private VCardBuilder lengthBuilder;
    private VCardBuilder builder;

    public VcardFormat() {
        lengthBuilder = new VCardBuilder(VCardConfig.VERSION_30);
        builder = new VCardBuilder(VCardConfig.VERSION_30);
    }

    private boolean appendable(int length) {
        if (lengthBuilder.toString().length() + length < MAX_LENGTH) {
            return true;
        } else {
            return false;
        }
    }

    private boolean appendable(List<ContentValues> entries, int tag) {
        if (entries == null || entries.size() == 0) {
            return false;
        }
        VCardBuilder vCardBuilder = new VCardBuilder(VCardConfig.VERSION_30);
        switch (tag) {
            case NICKNAME:
                vCardBuilder.appendNickNames(entries);
                break;
            case POSTAL:
                vCardBuilder.appendPostals(entries);
                break;
            case ORGANIZATION:
                vCardBuilder.appendOrganizations(entries);
                break;
            case WEBSITE:
                vCardBuilder.appendWebsites(entries);
                break;
            case NOTE:
                vCardBuilder.appendNotes(entries);
                break;
            case IMS:
                vCardBuilder.appendIms(entries);
                break;
            case EVENT:
                vCardBuilder.appendEvents(entries);
                break;
            case SIPADDRESS:
                vCardBuilder.appendSipAddresses(entries);
                break;
            case RELATION:
                vCardBuilder.appendRelation(entries);
                break;
            default:
                break;
        }
        int length = vCardBuilder.toString().length();
        return appendable(length);
    }

    /**
     * @param entries 待格式化的项
     * @param tag     可选NAME，EMAIL，PHONE
     * @return 返回格式化之后data1的总长度
     */
    private int formateData1(List<ContentValues> entries, int tag) {
        int length = 0;
        int max_size;
        int max_length;
        switch (tag) {
            case NAME:
                max_size = MAX_NAME_SIZE;
                max_length = MAX_NAME_LENGTH;
                break;
            case PHONE:
                max_size = MAX_PHONE_SIZE;
                max_length = MAX_PHONE_LENGTH;
                break;
            case EMAIL:
                max_size = MAX_EMAIL_SIZE;
                max_length = MAX_EMAIL_LENGTH;
                break;
            default:
                max_size = MAX_NAME_SIZE;
                max_length = MAX_NAME_LENGTH;
        }
        if (entries.size() > max_size) {
            entries = entries.subList(0, max_size);
        }
        for (ContentValues contentValue : entries) {
            String data1 = contentValue.getAsString(DATA1);
            if (data1.length() > max_length) {
                data1 = data1.substring(0, max_length);
                contentValue.put(DATA1, data1);
            }
            length += data1.length();
        }
        return length;
    }

    public VcardFormat appendNameProperties(List<ContentValues> nameEntries) {
        int length = formateData1(nameEntries, NAME);
        if (appendable(length)) {
            if (nameEntries.size() > MAX_NAME_SIZE) {
                nameEntries = nameEntries.subList(0, MAX_NAME_SIZE);
            }
            lengthBuilder.appendNameProperties(nameEntries);
            builder.appendNameProperties(nameEntries);
        }
        return this;
    }

    public VcardFormat appendPhones(List<ContentValues> phoneEntries) {
        int length = formateData1(phoneEntries, PHONE);
        if (appendable(length)) {
            if (phoneEntries.size() > MAX_PHONE_SIZE) {
                phoneEntries = phoneEntries.subList(0, MAX_PHONE_SIZE);
            }
            lengthBuilder.appendPhones(phoneEntries, null);
            builder.appendPhones(phoneEntries, null);
        }
        return this;
    }

    public VcardFormat appendEmails(List<ContentValues> emailEntries) {
        int length = formateData1(emailEntries, EMAIL);
        if (appendable(length)) {
            if (emailEntries.size() > MAX_EMAIL_SIZE) {
                emailEntries = emailEntries.subList(0, MAX_EMAIL_SIZE);
            }
            lengthBuilder.appendEmails(emailEntries);
            builder.appendEmails(emailEntries);
        }
        return this;
    }

    public VcardFormat appendNickNames(List<ContentValues> nicknameEntries) {
        if (appendable(nicknameEntries,NICKNAME)) {
            lengthBuilder.appendNickNames(nicknameEntries);
            builder.appendNickNames(nicknameEntries);
        }
        return this;
    }

    public VcardFormat appendPostals(List<ContentValues> postalEntries) {
        if (appendable(postalEntries,POSTAL)) {
            lengthBuilder.appendPostals(postalEntries);
            builder.appendPostals(postalEntries);
        }
        return this;
    }

    public VcardFormat appendOrganizations(List<ContentValues> organizationEntries) {
        if (appendable(organizationEntries,ORGANIZATION)) {
            lengthBuilder.appendOrganizations(organizationEntries);
            builder.appendOrganizations(organizationEntries);
        }
        return this;
    }

    public VcardFormat appendWebsites(List<ContentValues> webEntries) {
        if (appendable(webEntries,WEBSITE)) {
            lengthBuilder.appendWebsites(webEntries);
            builder.appendWebsites(webEntries);
        }
        return this;
    }

    public VcardFormat appendNotes(List<ContentValues> noteEntries) {
        if (appendable(noteEntries,NOTE)) {
            lengthBuilder.appendNotes(noteEntries);
            builder.appendNotes(noteEntries);
        }
        return this;
    }

    public VcardFormat appendIms(List<ContentValues> imEntries) {
        if (appendable(imEntries,IMS)) {
            lengthBuilder.appendIms(imEntries);
            builder.appendIms(imEntries);
        }
        return this;
    }

    public VcardFormat appendEvents(List<ContentValues> eventEntries) {
        if (appendable(eventEntries,EVENT)) {
            lengthBuilder.appendEvents(eventEntries);
            builder.appendEvents(eventEntries);
        }
        return this;
    }

    public VcardFormat appendSipAddresses(List<ContentValues> mSipEntries) {
        if (appendable(mSipEntries,SIPADDRESS)) {
            lengthBuilder.appendSipAddresses(mSipEntries);
            builder.appendSipAddresses(mSipEntries);
        }
        return this;
    }

    public VcardFormat appendRelation(List<ContentValues> relationEntries) {
        if (appendable(relationEntries,RELATION)) {
            lengthBuilder.appendRelation(relationEntries);
            builder.appendRelation(relationEntries);
        }
        return this;
    }

    public String end(){
        return builder.toString();
    }

    //参考使用
//    public static String buildVCard(ContactLoader.Result mContactData) {
//        if (mContactData == null) {
//            return "";
//        } else {
//            ArrayList<ContentValues> mOrganizationEntries = new ArrayList<ContentValues>();
//            ArrayList<ContentValues> mNicknameEntries = new ArrayList<ContentValues>();
//            ArrayList<ContentValues> mNameEntries = new ArrayList<ContentValues>();
//            ArrayList<ContentValues> mPhoneEntries = new ArrayList<ContentValues>();
//            ArrayList<ContentValues> mEmailEntries = new ArrayList<ContentValues>();
//            ArrayList<ContentValues> mImEntries = new ArrayList<ContentValues>();
//            ArrayList<ContentValues> mPostalEntries = new ArrayList<ContentValues>();
//            ArrayList<ContentValues> mNoteEntries = new ArrayList<ContentValues>();
//            ArrayList<ContentValues> mWebEntries = new ArrayList<ContentValues>();
//            ArrayList<ContentValues> mEventEntries = new ArrayList<ContentValues>();
//            ArrayList<ContentValues> mLunarBirthdayEntries = new ArrayList<ContentValues>();
//            ArrayList<ContentValues> mSipEntries = new ArrayList<ContentValues>();
//            ArrayList<ContentValues> mRelationEntries = new ArrayList<ContentValues>();
//
//            ArrayList<Entity> entities = mContactData.getEntities();
//            for (Entity entity : entities) {
//                for (Entity.NamedContentValues subValue : entity.getSubValues()) {
//                    final ContentValues entryValues = subValue.values;
//                    final String mimeType = entryValues.getAsString(ContactsContract.Data.MIMETYPE);
//                    final String data = entryValues.getAsString(ContactsContract.Data.DATA1);
//                    final boolean hasData = !TextUtils.isEmpty(data);
//
//                    if (Phone.CONTENT_ITEM_TYPE.equals(mimeType) && hasData) {
//                        // 电话
//                        mPhoneEntries.add(entryValues);
//                    } else if (Email.CONTENT_ITEM_TYPE.equals(mimeType) && hasData) {
//                        // 邮件
//                        mEmailEntries.add(entryValues);
//                    } else if (StructuredPostal.CONTENT_ITEM_TYPE.equals(mimeType) && hasData) {
//                        // 邮编
//                        mPostalEntries.add(entryValues);
//                    } else if (Im.CONTENT_ITEM_TYPE.equals(mimeType) && hasData) {
//                        mImEntries.add(entryValues);
//                    } else if (Organization.CONTENT_ITEM_TYPE.equals(mimeType) && hasData) {
//                        mOrganizationEntries.add(entryValues);
//                    } else if (Nickname.CONTENT_ITEM_TYPE.equals(mimeType) && hasData) {
//                        mNicknameEntries.add(entryValues);
//                    } else if (Note.CONTENT_ITEM_TYPE.equals(mimeType) && hasData) {
//                        mNoteEntries.add(entryValues);
//                    } else if (Website.CONTENT_ITEM_TYPE.equals(mimeType) && hasData) {
//                        mWebEntries.add(entryValues);
//                    } else if (StructuredName.CONTENT_ITEM_TYPE.equals(mimeType) && hasData) {
//                        mNameEntries.add(entryValues);
//                    } else if (Event.CONTENT_ITEM_TYPE.equals(mimeType) && hasData) {
//                        mEventEntries.add(entryValues);
//                    }  else if (SipAddress.CONTENT_ITEM_TYPE.equals(mimeType) && hasData) {
//                        mSipEntries.add(entryValues);
//                    } else if (Relation.CONTENT_ITEM_TYPE.equals(mimeType) && hasData) {
//                        mRelationEntries.add(entryValues);
//                    }
//                }
//            }
//
//            VcardFormat vcardFormat = new VcardFormat();
//            vcardFormat.appendNameProperties(mNameEntries)
//                    .appendPhones(mPhoneEntries)
//                    .appendEmails(mEmailEntries)
//                    .appendNickNames(mNicknameEntries)
//                    .appendPostals(mPostalEntries)
//                    .appendOrganizations(mOrganizationEntries)
//                    .appendWebsites(mWebEntries)
//                    .appendNotes(mNoteEntries)
//                    .appendIms(mImEntries)
//                    .appendEvents(mEventEntries)
//                    .appendSipAddresses(mSipEntries)
//                    .appendRelation(mRelationEntries);
//            return vcardFormat.end();
//        }
//    }

}
