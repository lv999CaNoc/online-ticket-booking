function validateDate(inputDate) {
    // Regex pattern to match dd/MM/yyyy format
    var datePattern = /^\d{2}\/\d{2}\/\d{4}$/;

    // Check if inputDate matches the pattern
    if (!datePattern.test(inputDate)) {
        return null; // Pattern doesn't match
    }

    // Further validation of the date components
    var parts = inputDate.split('/');
    var day = parseInt(parts[0], 10);
    var month = parseInt(parts[1], 10) - 1; // Month is 0-based in JavaScript Date object
    var year = parseInt(parts[2], 10);

    // Create a Date object and check if it represents a valid date
    var dateObj = new Date(year, month, day);
    if (dateObj.getFullYear() !== year || dateObj.getMonth() !== month || dateObj.getDate() !== day) {
        return null; // Invalid date (e.g., 31st February)
    }

    return dateObj; // Date is valid, return the Date object
}

function validateDateAndTime(inputDateTime) {
    // Regex pattern to match HH:mm dd/MM/yyyy format
    var dateTimePattern = /^([01]\d|2[0-3]):([0-5]\d)\s(\d{2})\/(\d{2})\/(\d{4})$/;

    // Check if inputDateTime matches the pattern
    if (!dateTimePattern.test(inputDateTime)) {
        return null; // Pattern doesn't match
    }

    // Further validation of the date and time components
    var parts = inputDateTime.split(' ');
    var timePart = parts[0];
    var datePart = parts[1];

    var timeParts = timePart.split(':');
    var hour = parseInt(timeParts[0], 10);
    var minute = parseInt(timeParts[1], 10);

    var dateParts = datePart.split('/');
    var day = parseInt(dateParts[0], 10);
    var month = parseInt(dateParts[1], 10) - 1; // Month is 0-based in JavaScript Date object
    var year = parseInt(dateParts[2], 10);

    // Create a Date object and check if it represents a valid date and time
    var dateObj = new Date(year, month, day, hour, minute);
    if (dateObj.getFullYear() !== year || dateObj.getMonth() !== month || dateObj.getDate() !== day ||
        dateObj.getHours() !== hour || dateObj.getMinutes() !== minute) {
        return null; // Invalid date or time (e.g., 31st February, 25:61)
    }

    return dateObj; // Date and time are valid, return the Date object
}


function validateEmail(email) {
    const re = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return re.test(String(email).toLowerCase());
}

function validatePhone(phone) {
    const re = /^[0-9]{10}$/;
    return re.test(phone);
}