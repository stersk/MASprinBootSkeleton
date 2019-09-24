function getLocalCurrencyRepresentation(value, withCurrencySymbol) {
  var currencyValue = value/100;
  var currencyRepresentation = '';

  if (withCurrencySymbol == undefined) {
      withCurrencySymbol = false;
  }

    if (toLocaleStringSupportsLocales()) {
      if (withCurrencySymbol) {
          currencyRepresentation = currencyValue.toLocaleString(undefined, {
              style: 'currency',
              currencyDisplay: 'symbol',
              currency: 'USD'
          });
      } else {
          currencyRepresentation = currencyValue.toLocaleString(undefined, {
              style: 'decimal',
              minimumFractionDigits: 2
          });
      }
  } else {
      currencyRepresentation = currencyValue.toFixed(2) + withCurrencySymbol ? ' $' : '';
  }

  return currencyRepresentation;
}

function parseLocalizedNumber(stringValue) {
    var numberString;
    var oneChar;
    var decimalFound = false;

    for (var i = stringValue.length - 1; i >= 0; i--) {
        oneChar = stringValue.charAt(i);
        if (!decimalFound && (oneChar == ',' || oneChar == '.')) {
            numberString = '.' + numberString;
            decimalFound = true;
        } else if (oneChar >= '0' && oneChar <= '9') {
            numberString = oneChar + numberString;
        }
    }

    return parseFloat(numberString);
}

function toLocaleStringSupportsLocales() {
    var number = 0;
    try {
        number.toLocaleString('i');
    } catch (e) {
        return e.name === 'RangeError';
    }
    return false;
}