function serializeArrayToJSON(formData) {
    var jsonData = {};

    formData.forEach(function(item) {
        jsonData[item.name] = item.value;
    });

    return jsonData;
}