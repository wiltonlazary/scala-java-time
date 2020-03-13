demo is used also to measure the size reductions of PRs

On sbt:
; clean; fastOptJS; fullOptJS
Command
ls -l demo/target/scala-2.13/*.js | ag opt | awk '{print $5, $6, $7, $8, $9}'

Baseline commit 2.0.0-RC5
2510190 Mar 13 10:30 demo/target/scala-2.13/demo-fastopt.js
500535 Mar 13 10:30 demo/target/scala-2.13/demo-opt.js
