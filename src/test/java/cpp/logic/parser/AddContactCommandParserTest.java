package cpp.logic.parser;

import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.logic.commands.AddContactCommand;
import cpp.logic.commands.CommandTestUtil;
import cpp.model.assignment.AssignmentName;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.contact.Address;
import cpp.model.contact.Contact;
import cpp.model.contact.ContactName;
import cpp.model.contact.Email;
import cpp.model.contact.Phone;
import cpp.model.tag.Tag;
import cpp.testutil.ContactBuilder;
import cpp.testutil.TypicalContacts;

public class AddContactCommandParserTest {
    private AddContactCommandParser parser = new AddContactCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Contact expectedContact = new ContactBuilder(TypicalContacts.BOB).withTags(CommandTestUtil.VALID_TAG_FRIEND)
                .build();

        // whitespace only preamble
        CommandParserTestUtil.assertParseSuccess(this.parser,
                CommandTestUtil.PREAMBLE_WHITESPACE + CommandTestUtil.NAME_DESC_BOB
                        + CommandTestUtil.PHONE_DESC_BOB + CommandTestUtil.EMAIL_DESC_BOB
                        + CommandTestUtil.ADDRESS_DESC_BOB + CommandTestUtil.TAG_DESC_FRIEND,
                new AddContactCommand(expectedContact));

        // multiple tags - all accepted
        Contact expectedContactMultipleTags = new ContactBuilder(TypicalContacts.BOB)
                .withTags(CommandTestUtil.VALID_TAG_FRIEND, CommandTestUtil.VALID_TAG_HUSBAND)
                .build();
        CommandParserTestUtil.assertParseSuccess(this.parser,
                CommandTestUtil.NAME_DESC_BOB + CommandTestUtil.PHONE_DESC_BOB
                        + CommandTestUtil.EMAIL_DESC_BOB + CommandTestUtil.ADDRESS_DESC_BOB
                        + CommandTestUtil.TAG_DESC_HUSBAND
                        + CommandTestUtil.TAG_DESC_FRIEND,
                new AddContactCommand(expectedContactMultipleTags));
    }

    @Test
    public void parse_repeatedNonTagValue_failure() {
        String validExpectedContactString = CommandTestUtil.NAME_DESC_BOB + CommandTestUtil.PHONE_DESC_BOB
                + CommandTestUtil.EMAIL_DESC_BOB
                + CommandTestUtil.ADDRESS_DESC_BOB + CommandTestUtil.TAG_DESC_FRIEND;

        // multiple names
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.NAME_DESC_AMY + validExpectedContactString,
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NAME));

        // multiple phones
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.PHONE_DESC_AMY + validExpectedContactString,
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_PHONE));

        // multiple emails
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.EMAIL_DESC_AMY + validExpectedContactString,
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_EMAIL));

        // multiple addresses
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.ADDRESS_DESC_AMY + validExpectedContactString,
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_ADDRESS));

        // multiple fields repeated
        CommandParserTestUtil.assertParseFailure(this.parser,
                validExpectedContactString + CommandTestUtil.PHONE_DESC_AMY
                        + CommandTestUtil.EMAIL_DESC_AMY + CommandTestUtil.NAME_DESC_AMY
                        + CommandTestUtil.ADDRESS_DESC_AMY
                        + validExpectedContactString,
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NAME,
                        CliSyntax.PREFIX_ADDRESS, CliSyntax.PREFIX_EMAIL,
                        CliSyntax.PREFIX_PHONE));

        // invalid value followed by valid value

        // invalid name
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.INVALID_NAME_DESC + validExpectedContactString,
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NAME));

        // invalid email
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.INVALID_EMAIL_DESC + validExpectedContactString,
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_EMAIL));

        // invalid phone
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.INVALID_PHONE_DESC + validExpectedContactString,
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_PHONE));

        // invalid address
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.INVALID_ADDRESS_DESC + validExpectedContactString,
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_ADDRESS));

        // valid value followed by invalid value

        // invalid name
        CommandParserTestUtil.assertParseFailure(this.parser,
                validExpectedContactString + CommandTestUtil.INVALID_NAME_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NAME));

        // invalid email
        CommandParserTestUtil.assertParseFailure(this.parser,
                validExpectedContactString + CommandTestUtil.INVALID_EMAIL_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_EMAIL));

        // invalid phone
        CommandParserTestUtil.assertParseFailure(this.parser,
                validExpectedContactString + CommandTestUtil.INVALID_PHONE_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_PHONE));

        // invalid address
        CommandParserTestUtil.assertParseFailure(this.parser,
                validExpectedContactString + CommandTestUtil.INVALID_ADDRESS_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_ADDRESS));
    }

    @Test
    public void parse_optionalFieldsMissing_success() {
        // zero tags
        Contact expectedContact = new ContactBuilder(TypicalContacts.AMY).withTags().build();
        CommandParserTestUtil.assertParseSuccess(this.parser,
                CommandTestUtil.NAME_DESC_AMY + CommandTestUtil.PHONE_DESC_AMY
                        + CommandTestUtil.EMAIL_DESC_AMY + CommandTestUtil.ADDRESS_DESC_AMY,
                new AddContactCommand(expectedContact));
    }

    @Test
    public void parse_optionalClassAndAssignmentPresent_success() {
        Contact expectedContact = new ContactBuilder(TypicalContacts.AMY).withTags().build();
        ClassGroupName expectedClassGroupName = new ClassGroupName("CS2103T10");
        AssignmentName expectedAssignmentName = new AssignmentName("Assignment 1");

        CommandParserTestUtil.assertParseSuccess(this.parser,
                CommandTestUtil.NAME_DESC_AMY + CommandTestUtil.PHONE_DESC_AMY
                        + CommandTestUtil.EMAIL_DESC_AMY + CommandTestUtil.ADDRESS_DESC_AMY
                        + " " + CliSyntax.PREFIX_CLASS + expectedClassGroupName.fullName
                        + " " + CliSyntax.PREFIX_ASSIGNMENT + expectedAssignmentName.fullName,
                new AddContactCommand(expectedContact, expectedClassGroupName, expectedAssignmentName));
    }

    @Test
    public void parse_optionalClassPresent_success() {
        Contact expectedContact = new ContactBuilder(TypicalContacts.AMY).withTags().build();
        ClassGroupName expectedClassGroupName = new ClassGroupName("CS2103T10");

        CommandParserTestUtil.assertParseSuccess(this.parser,
                CommandTestUtil.NAME_DESC_AMY + CommandTestUtil.PHONE_DESC_AMY
                        + CommandTestUtil.EMAIL_DESC_AMY + CommandTestUtil.ADDRESS_DESC_AMY
                        + " " + CliSyntax.PREFIX_CLASS + expectedClassGroupName.fullName,
                new AddContactCommand(expectedContact, expectedClassGroupName, null));
    }

    @Test
    public void parse_optionalAssignmentPresent_success() {
        Contact expectedContact = new ContactBuilder(TypicalContacts.AMY).withTags().build();
        AssignmentName expectedAssignmentName = new AssignmentName("Assignment 1");

        CommandParserTestUtil.assertParseSuccess(this.parser,
                CommandTestUtil.NAME_DESC_AMY + CommandTestUtil.PHONE_DESC_AMY
                        + CommandTestUtil.EMAIL_DESC_AMY + CommandTestUtil.ADDRESS_DESC_AMY
                        + " " + CliSyntax.PREFIX_ASSIGNMENT + expectedAssignmentName.fullName,
                new AddContactCommand(expectedContact, null, expectedAssignmentName));
    }

    @Test
    public void parse_repeatedOptionalValue_failure() {
        String validExpectedContactString = CommandTestUtil.NAME_DESC_BOB + CommandTestUtil.PHONE_DESC_BOB
                + CommandTestUtil.EMAIL_DESC_BOB + CommandTestUtil.ADDRESS_DESC_BOB;

        CommandParserTestUtil.assertParseFailure(this.parser,
                " " + CliSyntax.PREFIX_CLASS + "CS2103T10"
                        + validExpectedContactString
                        + " " + CliSyntax.PREFIX_CLASS + "CS2101T10",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_CLASS));

        CommandParserTestUtil.assertParseFailure(this.parser,
                " " + CliSyntax.PREFIX_ASSIGNMENT + "Assignment 1"
                        + validExpectedContactString
                        + " " + CliSyntax.PREFIX_ASSIGNMENT + "Assignment 2",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_ASSIGNMENT));
    }

    @Test
    public void parse_invalidOptionalValue_failure() {
        String validExpectedContactString = CommandTestUtil.NAME_DESC_BOB + CommandTestUtil.PHONE_DESC_BOB
                + CommandTestUtil.EMAIL_DESC_BOB + CommandTestUtil.ADDRESS_DESC_BOB;

        CommandParserTestUtil.assertParseFailure(this.parser,
                validExpectedContactString + CommandTestUtil.INVALID_CLASS_NAME_DESC,
                ClassGroupName.MESSAGE_CONSTRAINTS);

        CommandParserTestUtil.assertParseFailure(this.parser,
                validExpectedContactString + " " + CliSyntax.PREFIX_ASSIGNMENT + "&invalid",
                AssignmentName.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                AddContactCommand.MESSAGE_USAGE);

        // missing name prefix
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.VALID_NAME_BOB + CommandTestUtil.PHONE_DESC_BOB
                        + CommandTestUtil.EMAIL_DESC_BOB + CommandTestUtil.ADDRESS_DESC_BOB,
                expectedMessage);

        // missing phone prefix
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.NAME_DESC_BOB + CommandTestUtil.VALID_PHONE_BOB
                        + CommandTestUtil.EMAIL_DESC_BOB + CommandTestUtil.ADDRESS_DESC_BOB,
                expectedMessage);

        // missing email prefix
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.NAME_DESC_BOB + CommandTestUtil.PHONE_DESC_BOB
                        + CommandTestUtil.VALID_EMAIL_BOB + CommandTestUtil.ADDRESS_DESC_BOB,
                expectedMessage);

        // missing address prefix
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.NAME_DESC_BOB + CommandTestUtil.PHONE_DESC_BOB
                        + CommandTestUtil.EMAIL_DESC_BOB + CommandTestUtil.VALID_ADDRESS_BOB,
                expectedMessage);

        // all prefixes missing
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.VALID_NAME_BOB + CommandTestUtil.VALID_PHONE_BOB
                        + CommandTestUtil.VALID_EMAIL_BOB + CommandTestUtil.VALID_ADDRESS_BOB,
                expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.INVALID_NAME_DESC + CommandTestUtil.PHONE_DESC_BOB
                        + CommandTestUtil.EMAIL_DESC_BOB + CommandTestUtil.ADDRESS_DESC_BOB
                        + CommandTestUtil.TAG_DESC_HUSBAND + CommandTestUtil.TAG_DESC_FRIEND,
                ContactName.MESSAGE_CONSTRAINTS);

        // invalid phone
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.NAME_DESC_BOB + CommandTestUtil.INVALID_PHONE_DESC
                        + CommandTestUtil.EMAIL_DESC_BOB + CommandTestUtil.ADDRESS_DESC_BOB
                        + CommandTestUtil.TAG_DESC_HUSBAND + CommandTestUtil.TAG_DESC_FRIEND,
                Phone.MESSAGE_CONSTRAINTS);

        // invalid email
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.NAME_DESC_BOB + CommandTestUtil.PHONE_DESC_BOB
                        + CommandTestUtil.INVALID_EMAIL_DESC + CommandTestUtil.ADDRESS_DESC_BOB
                        + CommandTestUtil.TAG_DESC_HUSBAND + CommandTestUtil.TAG_DESC_FRIEND,
                Email.MESSAGE_CONSTRAINTS);

        // invalid address
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.NAME_DESC_BOB + CommandTestUtil.PHONE_DESC_BOB
                        + CommandTestUtil.EMAIL_DESC_BOB + CommandTestUtil.INVALID_ADDRESS_DESC
                        + CommandTestUtil.TAG_DESC_HUSBAND + CommandTestUtil.TAG_DESC_FRIEND,
                Address.MESSAGE_CONSTRAINTS);

        // invalid tag
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.NAME_DESC_BOB + CommandTestUtil.PHONE_DESC_BOB
                        + CommandTestUtil.EMAIL_DESC_BOB + CommandTestUtil.ADDRESS_DESC_BOB
                        + CommandTestUtil.INVALID_TAG_DESC + CommandTestUtil.VALID_TAG_FRIEND,
                Tag.MESSAGE_CONSTRAINTS);

        // two invalid values, only first invalid value reported
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.INVALID_NAME_DESC + CommandTestUtil.PHONE_DESC_BOB
                        + CommandTestUtil.EMAIL_DESC_BOB + CommandTestUtil.INVALID_ADDRESS_DESC,
                ContactName.MESSAGE_CONSTRAINTS);

        // non-empty preamble
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.PREAMBLE_NON_EMPTY + CommandTestUtil.NAME_DESC_BOB
                        + CommandTestUtil.PHONE_DESC_BOB + CommandTestUtil.EMAIL_DESC_BOB
                        + CommandTestUtil.ADDRESS_DESC_BOB + CommandTestUtil.TAG_DESC_HUSBAND
                        + CommandTestUtil.TAG_DESC_FRIEND,
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, AddContactCommand.MESSAGE_USAGE));
    }
}
