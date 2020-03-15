demo is used also to measure the size reductions of PRs

On sbt:
; clean; fastOptJS; fullOptJS
Command
ls -l demo/target/scala-2.13/*.js | ag opt | awk '{print $5, $6, $7, $8, $9}'

Baseline commit 2.0.0-RC5
2510190 Mar 13 10:30 demo/target/scala-2.13/demo-fastopt.js
500535 Mar 13 10:30 demo/target/scala-2.13/demo-opt.js

Commit 0759bf0ea98f1d34be251916290deef0ca3b3a46 (Expanded demo)
2595791 Mar 13 11:48 demo/target/scala-2.13/demo-fastopt.js
515115 Mar 13 11:48 demo/target/scala-2.13/demo-opt.js

Commit 293f2dab345e2dbfda025b361723a9abd4b0eaf2
2567763 Mar 15 19:37 demo/target/scala-2.13/demo-fastopt.js
508845 Mar 15 19:37 demo/target/scala-2.13/demo-opt.js

