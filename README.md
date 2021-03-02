# Parsers

## SUCCESS

Your[\s]*OneMoney[\s]*balance[\s]*is\:[\s]*RTGS[\s]*\$[\s]*(?<balance>[0-9\.\,]+)\.

You[\s]*have[\s]*(?<successfully>[A-Z, a-z, 0-9,\s])[\s]*sent[\s]*RTGS$ (?<amountSent>[0-9])[\s]*to[\s]*(?<receipientName>[A-Z, a-z, 0-9,\s]).Approval Code:\-(?<confirmationCode>[A-Z, a-z, 0-9,\.\s])

## FAILED

Wrong[\s]*PIN\,[\s]*please[\s]*check[\s]*and[\s]*try[\s]*again., OK

Email: info@zisky.co






