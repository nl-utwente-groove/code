<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
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
        <node id="n234">
            <attr name="layout">
                <string>181 228 26 30</string>
            </attr>
        </node>
        <node id="n236">
            <attr name="layout">
                <string>62 269 50 45</string>
            </attr>
        </node>
        <node id="n239">
            <attr name="layout">
                <string>65 66 43 30</string>
            </attr>
        </node>
        <node id="n235">
            <attr name="layout">
                <string>269 272 50 45</string>
            </attr>
        </node>
        <node id="n238">
            <attr name="layout">
                <string>115 169 26 30</string>
            </attr>
        </node>
        <node id="n237">
            <attr name="layout">
                <string>175 116 26 30</string>
            </attr>
        </node>
        <node id="n233">
            <attr name="layout">
                <string>239 171 26 30</string>
            </attr>
        </node>
        <node id="n240">
            <attr name="layout">
                <string>262 58 50 45</string>
            </attr>
        </node>
        <edge to="n234" from="n234">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
        <edge to="n234" from="n234">
            <attr name="label">
                <string>flag:free</string>
            </attr>
        </edge>
        <edge to="n236" from="n236">
            <attr name="label">
                <string>type:Phil</string>
            </attr>
        </edge>
        <edge to="n236" from="n236">
            <attr name="label">
                <string>flag:thinking</string>
            </attr>
        </edge>
        <edge to="n236" from="n236">
            <attr name="label">
                <string>let:forks = 0</string>
            </attr>
        </edge>
        <edge to="n234" from="n236">
            <attr name="label">
                <string>right</string>
            </attr>
        </edge>
        <edge to="n238" from="n236">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n239" from="n239">
            <attr name="label">
                <string>type:Phil</string>
            </attr>
        </edge>
        <edge to="n239" from="n239">
            <attr name="label">
                <string>flag:thinking</string>
            </attr>
        </edge>
        <edge to="n239" from="n239">
            <attr name="label">
                <string>let:forks = 0</string>
            </attr>
        </edge>
        <edge to="n237" from="n239">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n238" from="n239">
            <attr name="label">
                <string>right</string>
            </attr>
        </edge>
        <edge to="n235" from="n235">
            <attr name="label">
                <string>type:Phil</string>
            </attr>
        </edge>
        <edge to="n235" from="n235">
            <attr name="label">
                <string>flag:thinking</string>
            </attr>
        </edge>
        <edge to="n235" from="n235">
            <attr name="label">
                <string>let:forks = 0</string>
            </attr>
        </edge>
        <edge to="n233" from="n235">
            <attr name="label">
                <string>right</string>
            </attr>
        </edge>
        <edge to="n234" from="n235">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n238" from="n238">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
        <edge to="n238" from="n238">
            <attr name="label">
                <string>flag:free</string>
            </attr>
        </edge>
        <edge to="n237" from="n237">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
        <edge to="n237" from="n237">
            <attr name="label">
                <string>flag:free</string>
            </attr>
        </edge>
        <edge to="n233" from="n233">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
        <edge to="n233" from="n233">
            <attr name="label">
                <string>flag:free</string>
            </attr>
        </edge>
        <edge to="n240" from="n240">
            <attr name="label">
                <string>type:Phil</string>
            </attr>
        </edge>
        <edge to="n240" from="n240">
            <attr name="label">
                <string>flag:thinking</string>
            </attr>
        </edge>
        <edge to="n240" from="n240">
            <attr name="label">
                <string>let:forks = 0</string>
            </attr>
        </edge>
        <edge to="n237" from="n240">
            <attr name="label">
                <string>right</string>
            </attr>
        </edge>
        <edge to="n233" from="n240">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
    </graph>
</gxl>
