<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start-8">
        <attr name="transitionLabel">
            <string></string>
        </attr>
        <attr name="enabled">
            <string>true</string>
        </attr>
        <attr name="priority">
            <string>0</string>
        </attr>
        <attr name="printFormat">
            <string></string>
        </attr>
        <attr name="remark">
            <string></string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n281">
            <attr name="layout">
                <string>306 311 26 30</string>
            </attr>
        </node>
        <node id="n289">
            <attr name="layout">
                <string>507 90 50 45</string>
            </attr>
        </node>
        <node id="n276">
            <attr name="layout">
                <string>653 218 50 45</string>
            </attr>
        </node>
        <node id="n285">
            <attr name="layout">
                <string>340 238 26 30</string>
            </attr>
        </node>
        <node id="n277">
            <attr name="layout">
                <string>187 234 50 45</string>
            </attr>
        </node>
        <node id="n282">
            <attr name="layout">
                <string>328 106 50 45</string>
            </attr>
        </node>
        <node id="n287">
            <attr name="layout">
                <string>522 244 26 30</string>
            </attr>
        </node>
        <node id="n278">
            <attr name="layout">
                <string>213 409 43 30</string>
            </attr>
        </node>
        <node id="n288">
            <attr name="layout">
                <string>358 399 26 30</string>
            </attr>
        </node>
        <node id="n286">
            <attr name="layout">
                <string>674 425 50 45</string>
            </attr>
        </node>
        <node id="n280">
            <attr name="layout">
                <string>525 396 26 30</string>
            </attr>
        </node>
        <node id="n284">
            <attr name="layout">
                <string>564 323 26 30</string>
            </attr>
        </node>
        <node id="n279">
            <attr name="layout">
                <string>434 420 26 30</string>
            </attr>
        </node>
        <node id="n290">
            <attr name="layout">
                <string>327 520 50 45</string>
            </attr>
        </node>
        <node id="n283">
            <attr name="layout">
                <string>492 528 50 45</string>
            </attr>
        </node>
        <node id="n275">
            <attr name="layout">
                <string>437 211 26 30</string>
            </attr>
        </node>
        <edge to="n281" from="n281">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
        <edge to="n281" from="n281">
            <attr name="label">
                <string>flag:free</string>
            </attr>
        </edge>
        <edge to="n289" from="n289">
            <attr name="label">
                <string>type:Phil</string>
            </attr>
        </edge>
        <edge to="n289" from="n289">
            <attr name="label">
                <string>flag:thinking</string>
            </attr>
        </edge>
        <edge to="n289" from="n289">
            <attr name="label">
                <string>let:forks = 0</string>
            </attr>
        </edge>
        <edge to="n287" from="n289">
            <attr name="label">
                <string>right</string>
            </attr>
        </edge>
        <edge to="n275" from="n289">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n276" from="n276">
            <attr name="label">
                <string>type:Phil</string>
            </attr>
        </edge>
        <edge to="n276" from="n276">
            <attr name="label">
                <string>flag:thinking</string>
            </attr>
        </edge>
        <edge to="n276" from="n276">
            <attr name="label">
                <string>let:forks = 0</string>
            </attr>
        </edge>
        <edge to="n287" from="n276">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n284" from="n276">
            <attr name="label">
                <string>right</string>
            </attr>
        </edge>
        <edge to="n285" from="n285">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
        <edge to="n285" from="n285">
            <attr name="label">
                <string>flag:free</string>
            </attr>
        </edge>
        <edge to="n277" from="n277">
            <attr name="label">
                <string>type:Phil</string>
            </attr>
        </edge>
        <edge to="n277" from="n277">
            <attr name="label">
                <string>flag:thinking</string>
            </attr>
        </edge>
        <edge to="n277" from="n277">
            <attr name="label">
                <string>let:forks = 0</string>
            </attr>
        </edge>
        <edge to="n285" from="n277">
            <attr name="label">
                <string>right</string>
            </attr>
        </edge>
        <edge to="n281" from="n277">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n282" from="n282">
            <attr name="label">
                <string>type:Phil</string>
            </attr>
        </edge>
        <edge to="n282" from="n282">
            <attr name="label">
                <string>flag:thinking</string>
            </attr>
        </edge>
        <edge to="n282" from="n282">
            <attr name="label">
                <string>let:forks = 0</string>
            </attr>
        </edge>
        <edge to="n275" from="n282">
            <attr name="label">
                <string>right</string>
            </attr>
        </edge>
        <edge to="n285" from="n282">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n287" from="n287">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
        <edge to="n287" from="n287">
            <attr name="label">
                <string>flag:free</string>
            </attr>
        </edge>
        <edge to="n278" from="n278">
            <attr name="label">
                <string>type:Phil</string>
            </attr>
        </edge>
        <edge to="n278" from="n278">
            <attr name="label">
                <string>flag:thinking</string>
            </attr>
        </edge>
        <edge to="n278" from="n278">
            <attr name="label">
                <string>let:forks = 0</string>
            </attr>
        </edge>
        <edge to="n288" from="n278">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n281" from="n278">
            <attr name="label">
                <string>right</string>
            </attr>
        </edge>
        <edge to="n288" from="n288">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
        <edge to="n288" from="n288">
            <attr name="label">
                <string>flag:free</string>
            </attr>
        </edge>
        <edge to="n286" from="n286">
            <attr name="label">
                <string>type:Phil</string>
            </attr>
        </edge>
        <edge to="n286" from="n286">
            <attr name="label">
                <string>flag:thinking</string>
            </attr>
        </edge>
        <edge to="n286" from="n286">
            <attr name="label">
                <string>let:forks = 0</string>
            </attr>
        </edge>
        <edge to="n280" from="n286">
            <attr name="label">
                <string>right</string>
            </attr>
        </edge>
        <edge to="n284" from="n286">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n280" from="n280">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
        <edge to="n280" from="n280">
            <attr name="label">
                <string>flag:free</string>
            </attr>
        </edge>
        <edge to="n284" from="n284">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
        <edge to="n284" from="n284">
            <attr name="label">
                <string>flag:free</string>
            </attr>
        </edge>
        <edge to="n279" from="n279">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
        <edge to="n279" from="n279">
            <attr name="label">
                <string>flag:free</string>
            </attr>
        </edge>
        <edge to="n290" from="n290">
            <attr name="label">
                <string>type:Phil</string>
            </attr>
        </edge>
        <edge to="n290" from="n290">
            <attr name="label">
                <string>flag:thinking</string>
            </attr>
        </edge>
        <edge to="n290" from="n290">
            <attr name="label">
                <string>let:forks = 0</string>
            </attr>
        </edge>
        <edge to="n288" from="n290">
            <attr name="label">
                <string>right</string>
            </attr>
        </edge>
        <edge to="n279" from="n290">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n283" from="n283">
            <attr name="label">
                <string>type:Phil</string>
            </attr>
        </edge>
        <edge to="n283" from="n283">
            <attr name="label">
                <string>flag:thinking</string>
            </attr>
        </edge>
        <edge to="n283" from="n283">
            <attr name="label">
                <string>let:forks = 0</string>
            </attr>
        </edge>
        <edge to="n280" from="n283">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n279" from="n283">
            <attr name="label">
                <string>right</string>
            </attr>
        </edge>
        <edge to="n275" from="n275">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
        <edge to="n275" from="n275">
            <attr name="label">
                <string>flag:free</string>
            </attr>
        </edge>
    </graph>
</gxl>
