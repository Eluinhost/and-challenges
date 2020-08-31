# Challenge 4

How this works:

- `CurrencyAsWordsParser` uses parser combinators to parse and split a string like `'12345.67'` into `['1','2','3','4','5']` and `['6','7']`
whilst enforcing max length limits + allowing numbers greater than the Integer cap 
    - This part could have been done with a simple regex or even a `.dropUntil(_ == '.')` kind of thing but
    combinators allow for nicer error/failure output + expansion, and I wanted to try use them /shrug
- It then uses `IntegerToWords` to convert each part into words
- It then combines these 2 sets of words and adds 'pounds' and 'pence' as required

[Tests here](../../../../../../test/scala/digital/and/challenges/four)
