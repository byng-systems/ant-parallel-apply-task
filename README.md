ant-parallel-apply-task
=======================

Apache ANT task definition for applying an executable to multiple files in a controlled, multi-threaded environment, with a configurable number of threads.

I wrote the preliminary (forked to my own GitHub account) about 18 months ago after I read Sebastian Bergman's excellent book "Integrating PHP projects with Jenkins".  As an enthusiastic hybrid Java/PHP developer, I was very keen to bring this into my day job and side projects.

However I noticed that with large projects, utilising the <apply/> task to run lint (syntax) checks with a large codebase resulted in a massive wait time, and apply could not be used as effectively with the parallel wrapper.

My solution was to write this tool, taking advantage of controlled Java multi-threading as a generic alternative to the seemingly distinct <apply/> and <parallel/> tasks.  It was tested against a large codebase and benchmarked against <apply/> with the following results:
- <apply/> required 9 minutes and 40 seconds to process the entire codebase.
- <parallel-apply/> required 8 seconds to process the codebase.
