const fs = require('fs');

function main() {
  const args = process.argv.slice(2);
  if (args.length !== 1) {
    console.log('Usage: node genAst.js <output directory>');
    process.exit(64);
  }

  const [outputDir] = args;

  const types = [
    'Binary   : Expr left, Token operator, Expr right',
    'Grouping : Expr expression',
    'Literal  : Object value',
    'Unary    : Token operator, Expr right',
  ];

  defineAst(outputDir, 'Expr', types);
}

function defineAst(outputDir, baseName, types) {
  const path = `${outputDir}/${baseName}.java`;

  const writer = [];
  writer.push('package io.siever.lox;');
  writer.push('');
  writer.push('import java.util.List;');
  writer.push('');
  writer.push('abstract class ' + baseName + ' {');

  types.forEach(type => {
    writer.push('');
    const className = type.split(':')[0].trim();
    const fields = type.split(':')[1].trim();
    defineType(writer, baseName, className, fields);
  });

  writer.push('}');

  const sourceContent = writer.join('\n');
  fs.writeFileSync(path, sourceContent, { encoding: 'utf8' });
}

function defineType(writer, baseName, className, fieldList) {
  writer.push('  static class ' + className + ' extends ' + baseName + ' {');

  // Constructor.
  writer.push('    ' + className + '(' + fieldList + ') {');

  // Store parameters in fields.
  fields = fieldList.split(', ');
  fields.forEach(field => {
    name = field.split(' ')[1];
    writer.push('      this.' + name + ' = ' + name + ';');
  });

  writer.push('    }');

  // Fields.
  writer.push('');
  fields.forEach(field => {
    writer.push('    final ' + field + ';');
  });

  writer.push('  }');
}

main();
