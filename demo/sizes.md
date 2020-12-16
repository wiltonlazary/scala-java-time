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

Commit 85e334c196242d284408984853d941b305660dec
2568217 Apr 16 09:51 demo/target/scala-2.13/demo-fastopt.js
508853 Apr 16 09:52 demo/target/scala-2.13/demo-opt.js

# Note the increase when going to scala 2.13.4
Commit 3401b787bea943c8999ea606bb2c835ee3217fd0
2924794 Dec 16 00:38 demo/target/scala-2.13/demo-fastopt.js
574642 Dec 16 00:37 demo/target/scala-2.13/demo-opt.js

