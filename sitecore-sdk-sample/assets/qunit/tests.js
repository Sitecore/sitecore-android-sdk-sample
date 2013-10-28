var runAllTests = function () {
    test("ScMobile", function () {
        deepEqual(typeof scmobile, 'object', "scmobile object exists.");
    });

    module("ScMobile plugins");
    test("Console", function () {
        deepEqual(typeof scmobile.console, 'object', "'Console' plugin loaded.");
        deepEqual(typeof scmobile.console.log, 'function', "'Log' function exists.");
    });

    test("Device info", function () {
        ok(typeof scmobile.device !== 'undefined', "Device plugin loaded.");
        ok(typeof scmobile.device.version !== 'undefined', "Version is defined.");
        ok(typeof scmobile.device.name !== 'undefined', "Name is defined.");
        ok(typeof scmobile.device.uuid !== 'undefined', "Uuid is defined.");
        ok(typeof scmobile.device.something === 'undefined', "Other property undefined.");
    });

    test("Notification", function () {
        deepEqual(typeof scmobile.notification, 'object', "Notification plugin loaded.");

        deepEqual(typeof scmobile.notification.alert, 'function', "'alert' function exists.");
    });

    test("Accelerometer", function () {
        deepEqual(typeof scmobile.motion_manager, 'object', "'Accelerometer' plugin loaded.");

        deepEqual(typeof scmobile.motion_manager.Accelerometer, 'function', "'Accelerometer' class exists.");

        var accel = new scmobile.motion_manager.Accelerometer();
        deepEqual(typeof accel.start, 'function', "'Accelerometer.start' function exists.");
        deepEqual(typeof accel.stop, 'function', "'Accelerometer.stop' function exists.");

    });

    test("Camera", function () {
        deepEqual(typeof scmobile.camera, 'object', "'Camera' plugin loaded.");

        deepEqual(typeof scmobile.camera.getPicture, 'function', "'getPicture' function exists.");

    });

    test("Sharing", function () {
        deepEqual(typeof scmobile.share, 'object', "'Sharing' plugin loaded.");

        deepEqual(typeof scmobile.share.Email, 'function', "'Email' class exists.");
        deepEqual(typeof scmobile.share.Social, 'function', "'Social' class exists.");

        var email = new scmobile.share.Email();
        var social = new scmobile.share.Social();
        deepEqual(typeof email.send, 'function', "'Email.send' function exists.");
        deepEqual(typeof social.send, 'function', "'Social.send' function exists.");

    });

    test("Contacts", function () {
        deepEqual(typeof scmobile.contacts, 'object', "'Contacts' plugin loaded.");

        deepEqual(typeof scmobile.contacts.create, 'function', "'contacts.create' function exists.");

        if (scmobile.contacts.create) {
            var contact = scmobile.contacts.create();

            deepEqual(typeof contact.silentSave, 'function', "'contact.silentSave' function exists.");
            deepEqual(typeof contact.save, 'function', "'contact.save' function exists.");
        }

        deepEqual(typeof scmobile.contacts.silentSelect, 'function', "'scmobile.contacts.silentSelect' function exists.");
    });


    asyncTest("Create/find/edit/delete contact", function () {
        var onError = function (error) {
            console.log("Error: " + error.localizedMessage)
        }

        var origContact = {
            firstName: "QUnit" + Math.floor(Math.random() * 100),
            lastName: "Tester" + Math.floor(Math.random() * 100),
            phone1: "12345678",
            phone2: "87654321",
            company: "Sitecore",
            birthday: new Date()
        }

        var editedContact = {
            firstName: "Someone" + Math.floor(Math.random() * 100),
            lastName: "Another" + Math.floor(Math.random() * 100),
            phone: "999999",
            company: "AnotherCompany",
            birthday: new Date(1982, 2, 2)
        }

        var selectionUsed = false;
        var contactCreatedCallbackExecuted = false;
        var contactEditedCallbackExecuted = false;
        var contactDeletedCallbackExecuted = false;

        var contact = scmobile.contacts.create({
            firstName: origContact.firstName,
            lastName: origContact.lastName
        });

        contact.phones = [origContact.phone1, origContact.phone2];
        contact.company = origContact.company;
        contact.birthday = origContact.birthday;

        contact.silentSave(onContactSaved, onError);

        function onContactSaved() {
            contactCreatedCallbackExecuted = true;
            scmobile.contacts.silentSelect(selection, onContactFound, onError);
        }

        function selection(contact) {
            selectionUsed = true;
            return (contact.firstName === origContact.firstName && contact.lastName === origContact.lastName);
        }

        function editedSelection(contact) {
            selectionUsed = true;
            return (contact.firstName === editedContact.firstName && contact.lastName === editedContact.lastName);
        }


        function onContactFound(contacts) {
            for (var i = 0; i < contacts.length; i++) {
                var contact = contacts[i];
                scmobile.console.log("js found contact:" + JSON.stringify(contact));

                deepEqual(contact.firstName, origContact.firstName, "Select 'firstName' field.");
                deepEqual(contact.lastName, origContact.lastName, "Select 'lastName' field.");
                deepEqual(contact.company, origContact.company, "Select 'company' field.");
                deepEqual(contact.birthday, origContact.birthday, "Select 'birthday' field.");

                ok(contact.phones.indexOf(origContact.phone1) > -1, "Select 'phone1' field.");
                ok(contact.phones.indexOf(origContact.phone2) > -1, "Select 'phone2' field.");

                contact.firstName = editedContact.firstName;
                contact.lastName = editedContact.lastName;
                contact.company = editedContact.company;
                contact.birthday = editedContact.birthday;
                contact.phones = [editedContact.phone];

                contact.silentSave(onContactEdited, onError);
            }
        }

        function onContactEdited() {
            contactEditedCallbackExecuted = true;
            scmobile.contacts.silentSelect(editedSelection, function (contacts) {
                ok(contacts.length > 0, "Edited contact found.");
                for (var i = 0; i < contacts.length; i++) {
                    scmobile.console.log("js found contact:" + JSON.stringify(contacts[i]));
                    deepEqual(contacts[i].firstName, editedContact.firstName, "Edit 'firstName' field.");
                    deepEqual(contacts[i].lastName, editedContact.lastName, "Edit 'lastName' field.");
                    deepEqual(contacts[i].company, editedContact.company, "Edit 'company' field.");
                    deepEqual(contacts[i].birthday, editedContact.birthday, "Edit 'birthday' field.");

                    ok(contacts[i].phones.indexOf(editedContact.phone) > -1, "Edit 'phone' field.");
                    ok(contacts[i].phones.indexOf(origContact.phone1) === -1, "Old 'phone' deleted .");
                    ok(contacts[i].phones.indexOf(origContact.phone2) === -1, "Old 'phone' deleted .");

                    contacts[i].remove(onContactDeleted, onError);
                }
            }, onError);
        }

        function onContactDeleted() {
            contactDeletedCallbackExecuted = true;
            scmobile.contacts.silentSelect(editedSelection, onShouldNotBeFound, onError);
        }

        function onShouldNotBeFound(contacts) {
            deepEqual(contacts.length, 0, "Contact deleted.");
        }

        setTimeout(function () {
            ok(contactCreatedCallbackExecuted, "Contact created callback called.");
            ok(contactEditedCallbackExecuted, "Contact edited callback called.");
            ok(contactDeletedCallbackExecuted, "Contact delete callback called.")
            ok(selectionUsed, "Contact selection condition executed.");

            start();
        }, 10000);

    });

    function createContact(firstName, lastName, company, emails, phones, addresses, websites, birthday, photo, onSuccess) {
        var onError = function (error) {
            console.log("Error: " + error.localizedMessage);
            ok(false, "Contact is not created");
        }
        var contact = scmobile.contacts.create();
        if (firstName != null)
            contact.firstName = firstName;
        if (lastName != null)
            contact.lastName = lastName;
        if (company != null)
            contact.company = company;
        contact.emails = emails;
        contact.phones = phones;
        contact.addresses = addresses;
        contact.websites = websites;
        if (birthday != null)
            contact.birthday = birthday;
        if (photo != null)
            contact.photo = photo;

        contact.silentSave(onSuccess, onError);
    }

    function findContact(predicate, onSuccess) {
        var onError = function (error) {
            console.log("Error: " + error.localizedMessage);
            ok(false, "Contact is not found");
        }
        scmobile.contacts.silentSelect(predicate, onSuccess, onError);
    }

    function removeContacts(contacts) {
        var ids = [];
        for (var i = 0; i < contacts.length; i++) {
            ids.push(contacts[i].id);
        }
        scmobile.contacts.removeMultiple(ids, function(){}, function(){});
    }

    asyncTest("Create contact with first/last name", function () {
        var firstName = "First Name";
        var lastName = "Last Name";

        var predicate = function (contact) {
            return (contact.firstName == firstName && contact.lastName == lastName);
        }

        var onContactFound = function (contacts) {
            for (var i = 0; i < contacts.length; i++) {
                deepEqual(contacts[i].firstName, firstName, "'firstName' loaded correctly.");
                deepEqual(contacts[i].lastName, lastName, "'lastName' loaded correctly.");
            }
            removeContacts(contacts);
        }

        var onSuccess = function() {
            findContact(predicate, onContactFound);
        }

        createContact(firstName, lastName, null, null, null, null, null, null, null, onSuccess);

        setTimeout(function () {
            start();
        }, 2000);
    });

    asyncTest("Create contact with phone only", function () {
        var phone = "111222333";

        var predicate = function (contact) {
            return (contact.phones[0] == phone);
        }

        var onContactFound = function (contacts) {
            for (var i = 0; i < contacts.length; i++) {
                deepEqual(contacts[i].phones[0], phone, "'phone' loaded correctly.");
            }
            removeContacts(contacts);
        }

        var onSuccess = function() {
            findContact(predicate, onContactFound);
        }

        createContact(null, null, null, null, [phone], null, null, null, null, onSuccess);

        setTimeout(function () {
            start();
        }, 2000);
    });

    asyncTest("Create contact with all available fields", function () {
        var address = {
            street: "12, Street name",
            city: "City",
            state: "ST",
            zip: "123456",
            country: "France"
        }
        var birthday = '01.01.76';
        var firstName = 'AllFields';
        var lastName = 'SilentSave';
        var company = 'Company';
        var photo = "https://lh5.googleusercontent.com/-SA_DsSj1C_Y/AAAAAAAAAAI/AAAAAAAAAAA/FygjV1GbY1w/s96-c/photo.jpg";
        var emails = ['email1@gmail.com', 'email2@gmail.com', 'email3@gmail.com'];
        var phones = ['123456', '+38063 1234567'];
        var websites = ['website1.com', 'website2.com'];

        var predicate = function (contact) {
                return (contact.firstName == firstName);
            }
        var onContactFound = function (contacts) {
            for (var i = 0; i < contacts.length; i++) {
                deepEqual(contacts[i].firstName, firstName, "'firstName' loaded correctly.");
                deepEqual(contacts[i].lastName, lastName, "'lastName' loaded correctly.");
                deepEqual(contacts[i].company, company, "'company' loaded correctly.");
                deepEqual(contacts[i].emails.length, emails.length, "'emails' loaded correctly.");
                deepEqual(contacts[i].phones.length, phones.length, "'phones' loaded correctly.");
                deepEqual(contacts[i].websites.length, websites.length, "'websites' loaded correctly.");
                ok(contacts[i].birthday, "'birthday' loaded correctly.");
                ok(contacts[i].photo, "'photo' loaded correctly.");
                deepEqual(contacts[i].addresses.length, 1, "'addresses' loaded correctly.");
            }
            removeContacts(contacts);
        }
        var onSuccess = function() {
            findContact(predicate, onContactFound);
        }

        createContact(firstName, lastName, company, emails, phones, [address], websites, birthday, photo,
                onSuccess);
        setTimeout(function () {
            start();
        }, 2000);
    });

    asyncTest("Create contact with 10 phones", function () {
        var phones = ['123456', '666-666', '+380555555555', '(050)555-55-56', '+38 050 555 55 57', '+38(050)5555558', '33-44-55', '203', '056 555 55 59', '755-55-55'];

        var predicate = function (contact) {
            for (var i = 0; i < contact.phones.length; i++) {
                if (contact.phones[i] == '+380555555555')
                    return true;
            }
            return false;
        }

        var onContactFound = function (contacts) {
            for (var i = 0; i < contacts.length; i++) {
                deepEqual(contacts[i].phones.length, phones.length, "'phones' count = 10.");
            }
            removeContacts(contacts);
        }

        var onSuccess = function() {
            findContact(predicate, onContactFound);
        }

        createContact(null, null, null, null, phones, null, null, null, null, onSuccess);

        setTimeout(function () {
            start();
        }, 2000);
    });

    asyncTest("Create international contact", function () {
        var address1 = {
                street: "12, Глинки",
                city: "Днепропетровск",
                zip: "49000",
                country: "Україна"
            }
        var address2 = {
            city: "תל אביב",
            zip: "34000",
            country: "ישראל"
        }
        var company = '國際的';
        var firstName = 'International';
        var lastName = 'ระหว่างประเทศ';
        var websites = ['website1.com', 'россия.рф'];

        var predicate = function (contact) {
                return (contact.company == company);
            }
        var onContactFound = function (contacts) {
            for (var i = 0; i < contacts.length; i++) {
                deepEqual(contacts[i].firstName, firstName, "'firstName' loaded correctly.");
                deepEqual(contacts[i].lastName, lastName, "'lastName' loaded correctly.");
                deepEqual(contacts[i].company, company, "'company' loaded correctly.");
                deepEqual(contacts[i].websites.length, websites.length, "'websites' loaded correctly.");
                deepEqual(contacts[i].addresses.length, 2, "'addresses' loaded correctly.");
            }
            removeContacts(contacts);
        }
        var onSuccess = function() {
            findContact(predicate, onContactFound);
        }

        createContact(firstName, lastName, company, null, null, [address1, address2], websites, null, null,
                onSuccess);
        setTimeout(function () {
            start();
        }, 2000);
    });

    asyncTest("Create contact with very long fields", function () {
        var company = 'Krem ipsum dolor sit amet, consectetuer adipiscing elit. Nam cursus. Morbi ut mi. Nullam enim leo, egestas id, condimentum at, laoreet mattis, massa. Sed eleifend nonummy diam. Praesent mauris ante, elementum et, bibendum at, posuere sit amet, nibh.';
        var firstName = 'Dorem ipsum dolor sit amet, consectetuer adipiscing elit. Dam cursus. Dorbi ut mi. Nullam enim leo, egestas id, condimentum at, laoreet mattis, massa. Sed eleifend nonummy diam. Praesent mauris ante, elementum et, bibendum at, posuere sit amet, nibh.';
        var lastName = 'Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Nam cursus. Morbi ut mi. Nullam enim leo, egestas id, condimentum at, laoreet mattis, massa. Sed eleifend nonummy diam. Praesent mauris ante, elementum et, bibendum at, posuere sit amet, nibh. Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Nam cursus. Morbi ut mi. Nullam enim leo, egestas id, condimentum at, laoreet mattis, massa. Sed eleifend nonummy diam. Praesent mauris ante, elementum et, bibendum at, posuere sit amet, nibh';
        var phone = '12345689012345678909876543212345566778899098787655443';

        var newContact = scmobile.contacts.create();
        newContact.company = company;
        newContact.firstName = firstName;
        newContact.lastName = lastName;
        newContact.phones = [phone];

        var predicate = function (contact) {
            return (contact.company == company);
        }
        var onContactFound = function (contacts) {
            for (var i = 0; i < contacts.length; i++) {
                deepEqual(contacts[i].firstName, firstName, "'firstName' loaded correctly.");
                deepEqual(contacts[i].lastName, lastName, "'lastName' loaded correctly.");
                deepEqual(contacts[i].phones[0], phone, "'phone' loaded correctly.");
            }
            removeContacts(contacts);
        }

        var onSuccess = function() {
            findContact(predicate, onContactFound);
        }

        newContact.silentSave(onSuccess, function(){});

        setTimeout(function () {
            start();
        }, 2000);
    });

    asyncTest("Create contact with same phones", function () {
        var firstName = "SamePhones";
        var phones = ['223355', '223355'];

        var predicate = function (contact) {
                return (contact.firstName == firstName);
            }
        var onContactFound = function (contacts) {
            for (var i = 0; i < contacts.length; i++) {
                deepEqual(contacts[i].phones.length, 1, "'phones' merged correctly. (1 instead of 2)");
            }
            removeContacts(contacts);
        }
        var onSuccess = function() {
            findContact(predicate, onContactFound);
        }

        createContact(firstName, null, null, null, phones, null, null, null, null,
                onSuccess);
        setTimeout(function () {
            start();
        }, 2000);
    });

    asyncTest("Create contacts with same names", function () {
        var firstName = "SameContacts";

        var predicate = function (contact) {
                return (contact.firstName == firstName);
            }
        var onContactFound = function (contacts) {
            deepEqual(contacts.length, 2, "'contacts' merged correctly.");
            for (var i = 0; i < contacts.length; i++) {
                deepEqual(contacts[i].phones.length, 1, "'phones' merged correctly.");
            }
            removeContacts(contacts);
        }
        var onSuccess = function() {
            findContact(predicate, onContactFound);
        }
        var onError = function(error) {
            ok(false, "Contact is not created");
        }
        var onCreate = function() {
            var contact = scmobile.contacts.create();
            contact.firstName = firstName;
            contact.phones = ["998877"];
            contact.silentSave(onSuccess, onError);
        }

        var contact = scmobile.contacts.create();
        contact.firstName = firstName;
        contact.phones = ["778899"];
        contact.silentSave(onCreate, onError);

        setTimeout(function () {
            start();
        }, 2000);
    });

    asyncTest("Create and delete a contact", function () {
        var firstName = "ContactToDelete";

        var predicate = function (contact) {
                return (contact.firstName == firstName);
            }
        var onContactFound = function (contacts) {
            removeContacts(contacts);
            var checkDeleted = function(contacts) {
                 deepEqual(contacts.length, 0, "'contact' deleted successfully");
            }
            findContact(predicate, checkDeleted);
        }
        var onSuccess = function() {
            findContact(predicate, onContactFound);
        }

        createContact(firstName, null, null, null, null, null, null, null, null,
                onSuccess);
        setTimeout(function () {
            start();
        }, 2000);
    });
}
