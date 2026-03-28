package cpp.logic.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.logic.parser.exceptions.ParseException;
import cpp.model.assignment.AssignmentName;
import cpp.model.contact.Address;
import cpp.model.contact.ContactName;
import cpp.model.contact.Email;
import cpp.model.contact.Phone;
import cpp.model.tag.Tag;
import cpp.testutil.Assert;
import cpp.testutil.TypicalIndexes;

public class ParserUtilTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";
    private static final String INVALID_DEADLINE = "12-13-2020 10:00";
    private static final String INVALID_ASSIGNMENT_NAME = " ";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "123456";
    private static final String VALID_ADDRESS = "123 Main Street #0505";
    private static final String VALID_EMAIL = "rachel@example.com";
    private static final String VALID_TAG_1 = "friend";
    private static final String VALID_TAG_2 = "neighbour";
    private static final String VALID_DEADLINE = "13-12-2020 10:00";
    private static final String VALID_ASSIGNMENT_NAME = "Assignment 1";

    private static final String WHITESPACE = " \t\r\n";

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        Assert.assertThrows(ParseException.class, () -> ParserUtil.parseIndex("10 a"));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        Assert.assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_INDEX,
                () -> ParserUtil.parseIndex(Long.toString(Integer.MAX_VALUE + 1)));
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        Assertions.assertEquals(TypicalIndexes.INDEX_FIRST_CONTACT, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        Assertions.assertEquals(TypicalIndexes.INDEX_FIRST_CONTACT, ParserUtil.parseIndex("  1  "));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        Assert.assertThrows(ParseException.class, () -> ParserUtil.parseName(ParserUtilTest.INVALID_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        ContactName expectedName = new ContactName(ParserUtilTest.VALID_NAME);
        Assertions.assertEquals(expectedName, ParserUtil.parseName(ParserUtilTest.VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = ParserUtilTest.WHITESPACE + ParserUtilTest.VALID_NAME + ParserUtilTest.WHITESPACE;
        ContactName expectedName = new ContactName(ParserUtilTest.VALID_NAME);
        Assertions.assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parsePhone_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> ParserUtil.parsePhone((String) null));
    }

    @Test
    public void parsePhone_invalidValue_throwsParseException() {
        Assert.assertThrows(ParseException.class, () -> ParserUtil.parsePhone(ParserUtilTest.INVALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithoutWhitespace_returnsPhone() throws Exception {
        Phone expectedPhone = new Phone(ParserUtilTest.VALID_PHONE);
        Assertions.assertEquals(expectedPhone, ParserUtil.parsePhone(ParserUtilTest.VALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithWhitespace_returnsTrimmedPhone() throws Exception {
        String phoneWithWhitespace = ParserUtilTest.WHITESPACE + ParserUtilTest.VALID_PHONE + ParserUtilTest.WHITESPACE;
        Phone expectedPhone = new Phone(ParserUtilTest.VALID_PHONE);
        Assertions.assertEquals(expectedPhone, ParserUtil.parsePhone(phoneWithWhitespace));
    }

    @Test
    public void parseAddress_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> ParserUtil.parseAddress((String) null));
    }

    @Test
    public void parseAddress_invalidValue_throwsParseException() {
        Assert.assertThrows(ParseException.class, () -> ParserUtil.parseAddress(ParserUtilTest.INVALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithoutWhitespace_returnsAddress() throws Exception {
        Address expectedAddress = new Address(ParserUtilTest.VALID_ADDRESS);
        Assertions.assertEquals(expectedAddress, ParserUtil.parseAddress(ParserUtilTest.VALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithWhitespace_returnsTrimmedAddress() throws Exception {
        String addressWithWhitespace = ParserUtilTest.WHITESPACE + ParserUtilTest.VALID_ADDRESS
                + ParserUtilTest.WHITESPACE;
        Address expectedAddress = new Address(ParserUtilTest.VALID_ADDRESS);
        Assertions.assertEquals(expectedAddress, ParserUtil.parseAddress(addressWithWhitespace));
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail((String) null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        Assert.assertThrows(ParseException.class,
                () -> ParserUtil.parseEmail(ParserUtilTest.INVALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
        Email expectedEmail = new Email(ParserUtilTest.VALID_EMAIL);
        Assertions.assertEquals(expectedEmail, ParserUtil.parseEmail(ParserUtilTest.VALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String emailWithWhitespace = ParserUtilTest.WHITESPACE + ParserUtilTest.VALID_EMAIL + ParserUtilTest.WHITESPACE;
        Email expectedEmail = new Email(ParserUtilTest.VALID_EMAIL);
        Assertions.assertEquals(expectedEmail, ParserUtil.parseEmail(emailWithWhitespace));
    }

    @Test
    public void parseTag_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> ParserUtil.parseTag(null));
    }

    @Test
    public void parseTag_invalidValue_throwsParseException() {
        Assert.assertThrows(ParseException.class, () -> ParserUtil.parseTag(ParserUtilTest.INVALID_TAG));
    }

    @Test
    public void parseTag_validValueWithoutWhitespace_returnsTag() throws Exception {
        Tag expectedTag = new Tag(ParserUtilTest.VALID_TAG_1);
        Assertions.assertEquals(expectedTag, ParserUtil.parseTag(ParserUtilTest.VALID_TAG_1));
    }

    @Test
    public void parseTag_validValueWithWhitespace_returnsTrimmedTag() throws Exception {
        String tagWithWhitespace = ParserUtilTest.WHITESPACE + ParserUtilTest.VALID_TAG_1 + ParserUtilTest.WHITESPACE;
        Tag expectedTag = new Tag(ParserUtilTest.VALID_TAG_1);
        Assertions.assertEquals(expectedTag, ParserUtil.parseTag(tagWithWhitespace));
    }

    @Test
    public void parseTags_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> ParserUtil.parseTags(null));
    }

    @Test
    public void parseTags_collectionWithInvalidTags_throwsParseException() {
        Assert.assertThrows(ParseException.class,
                () -> ParserUtil.parseTags(Arrays.asList(ParserUtilTest.VALID_TAG_1, ParserUtilTest.INVALID_TAG)));
    }

    @Test
    public void parseTags_emptyCollection_returnsEmptySet() throws Exception {
        Assertions.assertTrue(ParserUtil.parseTags(Collections.emptyList()).isEmpty());
    }

    @Test
    public void parseTags_collectionWithValidTags_returnsTagSet() throws Exception {
        Set<Tag> actualTagSet = ParserUtil
                .parseTags(Arrays.asList(ParserUtilTest.VALID_TAG_1, ParserUtilTest.VALID_TAG_2));
        Set<Tag> expectedTagSet = new HashSet<Tag>(
                Arrays.asList(new Tag(ParserUtilTest.VALID_TAG_1), new Tag(ParserUtilTest.VALID_TAG_2)));

        Assertions.assertEquals(expectedTagSet, actualTagSet);
    }

    @Test
    public void parseDeadline_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> ParserUtil.parseDeadline((String) null));
    }

    @Test
    public void parseDeadline_invalidValue_throwsParseException() {
        Assert.assertThrows(ParseException.class,
                () -> ParserUtil.parseDeadline(ParserUtilTest.INVALID_DEADLINE));
    }

    @Test
    public void parseDeadline_validValueWithoutWhitespace_returnsLocalDateTime() throws Exception {
        LocalDateTime expected = LocalDateTime.parse(ParserUtilTest.VALID_DEADLINE,
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        ;
        Assertions.assertEquals(expected, ParserUtil.parseDeadline(ParserUtilTest.VALID_DEADLINE));
    }

    @Test
    public void parseDeadline_validValueWithWhitespace_returnsTrimmedLocalDateTime() throws Exception {
        String datetimeWithWhitespace = ParserUtilTest.WHITESPACE + ParserUtilTest.VALID_DEADLINE
                + ParserUtilTest.WHITESPACE;
        LocalDateTime expected = LocalDateTime.parse(ParserUtilTest.VALID_DEADLINE,
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        Assertions.assertEquals(expected, ParserUtil.parseDeadline(datetimeWithWhitespace));
    }

    @Test
    public void parseDateTime_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> ParserUtil.parseDateTime((String) null));
    }

    @Test
    public void parseDateTime_invalidValue_throwsParseException() {
        Assert.assertThrows(ParseException.class,
                () -> ParserUtil.parseDateTime(ParserUtilTest.INVALID_DEADLINE));
    }

    @Test
    public void parseDateTime_validValueWithoutWhitespace_returnsLocalDateTime() throws Exception {
        LocalDateTime expected = LocalDateTime.parse(ParserUtilTest.VALID_DEADLINE,
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        Assertions.assertEquals(expected, ParserUtil.parseDateTime(ParserUtilTest.VALID_DEADLINE));
    }

    @Test
    public void parseDateTime_invalidFutureDateTime_throwsParseException() {
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(1);
        String futureDateTimeString = futureDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        Assert.assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_FUTURE_DATETIME,
                () -> ParserUtil.parseDateTime(futureDateTimeString));
    }

    @Test
    public void parseAssignmentName_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> ParserUtil.parseAssignmentName((String) null));
    }

    @Test
    public void parseAssignmentName_invalidValue_throwsParseException() {
        Assert.assertThrows(ParseException.class,
                () -> ParserUtil.parseAssignmentName(ParserUtilTest.INVALID_ASSIGNMENT_NAME));
    }

    @Test
    public void parseAssignmentName_validValueWithoutWhitespace_returnsName() throws Exception {
        AssignmentName expectedName = new AssignmentName(
                ParserUtilTest.VALID_ASSIGNMENT_NAME);
        Assertions.assertEquals(expectedName, ParserUtil.parseAssignmentName(ParserUtilTest.VALID_ASSIGNMENT_NAME));
    }

    @Test
    public void parseAssignmentName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = ParserUtilTest.WHITESPACE + ParserUtilTest.VALID_ASSIGNMENT_NAME
                + ParserUtilTest.WHITESPACE;
        AssignmentName expectedName = new AssignmentName(
                ParserUtilTest.VALID_ASSIGNMENT_NAME);
        Assertions.assertEquals(expectedName, ParserUtil.parseAssignmentName(nameWithWhitespace));
    }
}
