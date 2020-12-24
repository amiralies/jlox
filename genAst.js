const fs = require('fs');

function main() {
  const args = process.argv.slice(2);
  if (args.length !== 1) {
    console.log('Usage: node genAst.js <output directory>');
    process.exit(64);
  }

  const [outputDir] = args;

  const exprTypes = [
    'Assign       : Token name, Expr value',
    'Binary       : Expr left, Token operator, Expr right',
    'Conditional  : Expr condition, Expr thenBranch, Expr elseBranch',
    'Grouping     : Expr expression',
    'Literal      : Object value',
    'Unary        : Token operator, Expr right',
    'Variable     : Token name',
  ];
  exprImports = [];

  const stmtTypes = [
    // Block statement
    'Block      : List<Stmt> statements',
    // Statement expression
    'Expression : Expr expression',
    // If statement
    'If         : Expr condition, Stmt thenBranch, Stmt elseBranch',
    // Print statement
    'Print      : Expr expression',
    // Var declaration statement
    'Var        : Token name, Expr initializer',
  ];
  const stmtImports = ['java.util.List'];

  defineAst(outputDir, 'Expr', exprTypes, exprImports);
  defineAst(outputDir, 'Stmt', stmtTypes, stmtImports);
}

function defineAst(outputDir, baseName, types, imports) {
  const path = `${outputDir}/${baseName}.java`;

  const writer = [];
  writer.push('package io.siever.lox;');
  writer.push('');
  imports.forEach(item => {
    writer.push(`import ${item};`);
  });
  writer.push('');
  writer.push('abstract class ' + baseName + ' {');

  defineVistor(writer, baseName, types);

  types.forEach(type => {
    writer.push('');
    const className = type.split(':')[0].trim();
    const fields = type.split(':')[1].trim();
    defineType(writer, baseName, className, fields);
  });

  // The base accept() method.
  writer.push('');
  writer.push('  abstract <R> R accept(Visitor<R> visitor);');

  writer.push('}');

  const sourceContent = writer.join('\n');
  fs.writeFileSync(path, sourceContent, { encoding: 'utf8' });
}

function defineVistor(writer, baseName, types) {
  writer.push('  interface Visitor<R> {');

  types.forEach(type => {
    let typeName = type.split(':')[0].trim();
    writer.push('    R visit' + typeName + baseName + '(' + typeName + ' ' + baseName.toLowerCase() + ');');
  });

  writer.push('  }');
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

  // Visitor pattern.
  writer.push('');
  writer.push('    @Override');
  writer.push('    <R> R accept(Visitor<R> visitor) {');
  writer.push('      return visitor.visit' + className + baseName + '(this);');
  writer.push('    }');

  // Fields.
  writer.push('');
  fields.forEach(field => {
    writer.push('    final ' + field + ';');
  });

  writer.push('  }');
}

main();
