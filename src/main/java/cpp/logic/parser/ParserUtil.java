package cpp.logic.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import cpp.commons.core.index.Index;
import cpp.commons.util.StringUtil;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.assignment.AssignmentName;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.contact.Address;
import cpp.model.contact.ContactName;
import cpp.model.contact.Email;
import cpp.model.contact.Phone;
import cpp.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser
 * classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index is not a non-zero unsigned integer.";
    public static final String MESSAGE_EMPTY_INDICES = "Contact indices should not be blank.";
    public static final DateTimeFormatter DEADLINE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading
     * and trailing whitespaces will be
     * trimmed.
     *
     * @throws ParseException if the specified index is invalid (not non-zero
     *                        unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(ParserUtil.MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static ContactName parseName(String name) throws ParseException {
        Objects.requireNonNull(name);
        String trimmedName = name.trim();
        if (!ContactName.isValidName(trimmedName)) {
            throw new ParseException(ContactName.MESSAGE_CONSTRAINTS);
        }
        return new ContactName(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        Objects.requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        Objects.requireNonNull(address);
        String trimmedAddress = address.trim();
        if (!Address.isValidAddress(trimmedAddress)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        Objects.requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        Objects.requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        Objects.requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(ParserUtil.parseTag(tagName));
        }
        return tagSet;
    }

    /**
     * Parses a {@code String datetime} into a {@code LocalDateTime}.
     */
    public static LocalDateTime parseDeadline(String datetime) throws ParseException {
        Objects.requireNonNull(datetime);
        String trimmedDatetime = datetime.trim().replaceAll("\\s+", " ").trim();
        LocalDateTime parsedDateTime;
        try {
            parsedDateTime = LocalDateTime.parse(trimmedDatetime, ParserUtil.DEADLINE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ParseException("Invalid date and time format. Please use the format: dd-MM-yyyy HH:mm");
        }
        return parsedDateTime;
    }

    /**
     * Parses a {@code String name} into a {@code AssignmentName}.
     */
    public static AssignmentName parseAssignmentName(String string) throws ParseException {
        Objects.requireNonNull(string);
        String trimmedName = string.trim().replaceAll("\\s+", " ");
        if (!AssignmentName.isValidName(trimmedName)) {
            throw new ParseException(AssignmentName.MESSAGE_CONSTRAINTS);
        }
        return new AssignmentName(trimmedName);
    }

    /**
     * Parses a {@code String name} into a {@code ClassGroupName}.
     */
    public static ClassGroupName parseClassGroupName(String string) throws ParseException {
        Objects.requireNonNull(string);
        String trimmedName = string.trim().replaceAll("\\s+", " ");
        if (!ClassGroupName.isValidName(trimmedName)) {
            throw new ParseException(ClassGroupName.MESSAGE_CONSTRAINTS);
        }
        return new ClassGroupName(trimmedName);
    }

    /**
     * Parses the contact indices from the given contact value string.
     * The contact value string is expected to contain space-separated indices.
     * Example: "1 2 3" will be parsed into a list of indices [1, 2, 3].
     * If the contact value string is empty or contains only whitespace, an empty
     * list will be returned.
     * If the contact value string contains duplicate indices, it will only return
     * the unique indices.
     *
     * @param contactValue the string containing the contact indices to be parsed
     * @return a list of indices parsed from the contact value string
     * @throws ParseException if any of the contact indices are not valid integers
     *                        or are out of bounds
     */
    public static List<Index> parseContactIndices(String contactValue) throws ParseException {
        String[] parts = contactValue.trim().split("\\s+");
        Set<Index> contactIndices = new LinkedHashSet<>();
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            contactIndices.add(ParserUtil.parseIndex(part));
        }
        return new ArrayList<>(contactIndices);
    }
}
