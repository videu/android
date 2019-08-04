# DEvid Android Contribution Guide

First things first: I would like to humbly thank you for wanting to support the
DEvid project.  You’re awesome!

## Environment Setup

This guide has moved to `doc/dev-setup.md`.

## Code Style

These guidelines build upon the
[AOSP Java Code Styleguide](https://source.android.com/setup/contribute/code-style),
so please read that first.  Your default Android Studio installation will have
most code style-related settings right anyways, so don't worry.

The general line length limit is, as you should know now, 100 characters.
If a method has a parameter list too long to fit on one line, just break the
parameters up and align them to the opening parenthesis.  When calling another
method and the parameter list does not fit on one line, write every parameter
on a separate one and indent them with 8 spaces.  Furthermore, please leave a
blank line between class heads and the actual class content.  Here's a good
example code snippet that demonstrates what I just wrote:

```java
public class Foo {
                    // < Blank line between class head and methods
    public int makeThatStackMemoryFull(int parameterOne, int parameterTwo, int theThirdParameter,
                                       double anotherParameter, boolean theLastOne) {
        // Parameters aligned properly ^
        
        return makeThatStackMemoryFull(
                parameterOne, // < Parameters that wouldn't fit on one line are
                parameterTwo, //   split and double-indented (8 spaces)
                theThirdParameter,
                anotherParameter,
                theLastOne
        ); // < The closing parenthesis takes one line on its own
    }
                    // < Another blank line before closing the class body
}
```

## Commit Messages

Commit messages should be brief but descriptive, you know the drill.  Also, try
to avoid curse words if they're not necessary (even though it is fine to
immortalize your frustration in the commit message sometimes :D).
