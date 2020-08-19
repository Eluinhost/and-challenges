const interface = require('readline').createInterface({input: process.stdin, output: process.stdout, terminal: false });

interface.question('To prove you are a human solve the following formula: 1 + 1\n> ', result => {
    process.exit(parseInt(result, 10));
});

