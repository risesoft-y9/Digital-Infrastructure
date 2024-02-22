/* JavaScript中Base64编码 */
var keyStr = "ABCDEFGHIJKLMNOP" + "QRSTUVWXYZabcdef" + "ghijklmnopqrstuv"
    + "wxyz0123456789+/" + "=";


function unicodetoBytes(s) {
    var result = new Array();
    if (s == null || s == "")
        return result;
    result.push(255); // add "FE" to head 
    result.push(254);
    for (var i = 0; i < s.length; i++) {
        var c = s.charCodeAt(i).toString(16);
        if (c.length == 1)
            i = "000" + c;
        else if (c.length == 2)
            c = "00" + c;
        else if (c.length == 3)
            c = "0" + c;
        var var1 = parseInt(c.substring(2), 16);
        var var2 = parseInt(c.substring(0, 2), 16);
        result.push(var1);
        result.push(var2);
    }
    return result;
}

function bytesToUnicode(bs) {
    var result = "";
    var offset = 0;
    if (bs.length >= 2 && bs[0] == 255 && bs[1] == 254)
        offset = 2; // delete "FE" 
    for (var i = offset; i < bs.length; i += 2) {
        var code = bs[i] + (bs[i + 1] << 8);
        result += String.fromCharCode(code);
    }
    return result;
}

export function encode64(input) {
    if (input == undefined || input == "") return "";
    input = unicodetoBytes(input);
    var output = "";
    var chr1, chr2, chr3 = "";
    var enc1, enc2, enc3, enc4 = "";
    var i = 0;

    do {
        chr1 = input[i++];
        chr2 = input[i++];
        chr3 = input[i++];

        enc1 = chr1 >> 2;
        enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
        enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
        enc4 = chr3 & 63;

        if (isNaN(chr2)) {
            enc3 = enc4 = 64;
        } else if (isNaN(chr3)) {
            enc4 = 64;
        }

        output = output + keyStr.charAt(enc1) + keyStr.charAt(enc2)
            + keyStr.charAt(enc3) + keyStr.charAt(enc4);
        chr1 = chr2 = chr3 = "";
        enc1 = enc2 = enc3 = enc4 = "";
    } while (i < input.length);

    return output;
}

export function decode64(input) {
    if (input == undefined || input == "") return "";
    var output = "";
    var chr1, chr2, chr3 = "";
    var enc1, enc2, enc3, enc4 = "";
    var i = 0;

    // remove all characters that are not A-Z, a-z, 0-9, +, /, or = 
    var base64test = /[^A-Za-z0-9/+///=]/g;
    if (base64test.exec(input)) {
        alert("There were invalid base64 characters in the input text./n"
            + "Valid base64 characters are A-Z, a-z, 0-9, '+', '/', and '='/n"
            + "Expect errors in decoding.");
    }
    input = input.replace(/[^A-Za-z0-9/+///=]/g, "");
    output = new Array();
    do {
        enc1 = keyStr.indexOf(input.charAt(i++));
        enc2 = keyStr.indexOf(input.charAt(i++));
        enc3 = keyStr.indexOf(input.charAt(i++));
        enc4 = keyStr.indexOf(input.charAt(i++));

        chr1 = (enc1 << 2) | (enc2 >> 4);
        chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
        chr3 = ((enc3 & 3) << 6) | enc4;

        output.push(chr1);
        if (enc3 != 64) {
            output.push(chr2);
        }
        if (enc4 != 64) {
            output.push(chr3);
        }

        chr1 = chr2 = chr3 = "";
        enc1 = enc2 = enc3 = enc4 = "";

    } while (i < input.length);
    return bytesToUnicode(output);
}