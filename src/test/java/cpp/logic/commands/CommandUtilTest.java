package cpp.logic.commands;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.commons.core.index.Index;
import cpp.logic.commands.exceptions.CommandException;
import cpp.model.contact.Contact;
import cpp.testutil.ContactBuilder;

public class CommandUtilTest {
    @Test
    public void checkContactIndices_validIndices_doesNotThrow() {
        List<Contact> lastShownContactList = List.of(
                new ContactBuilder().withName("Alice").build(),
                new ContactBuilder().withName("Bob").build(),
                new ContactBuilder().withName("Charlie").build());

        List<Index> contactIndices = List.of(Index.fromOneBased(1), Index.fromOneBased(2), Index.fromOneBased(3));

        Assertions.assertDoesNotThrow(() -> CommandUtil.checkContactIndices(lastShownContactList, contactIndices));
    }

    @Test
    public void checkContactIndices_invalidIndex_throwsCommandException() {
        List<Contact> lastShownContactList = List.of(
                new ContactBuilder().withName("Alice").build(),
                new ContactBuilder().withName("Bob").build(),
                new ContactBuilder().withName("Charlie").build());

        List<Index> contactIndices = List.of(Index.fromOneBased(1), Index.fromOneBased(4));

        Assertions.assertThrows(CommandException.class,
                () -> CommandUtil.checkContactIndices(lastShownContactList, contactIndices));
    }
}
