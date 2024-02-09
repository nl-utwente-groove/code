<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="twoAOneB">
        <node id="n0">
            <attr name="layout">
                <string>193 208 43 31</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>121 143 28 31</string>
            </attr>
        </node>
        <node id="n7">
            <attr name="layout">
                <string>311 257 28 31</string>
            </attr>
        </node>
        <node id="n8">
            <attr name="layout">
                <string>253 63 28 32</string>
            </attr>
        </node>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>Pool</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>A</string>
            </attr>
        </edge>
        <edge to="n0" from="n5">
            <attr name="label">
                <string>in</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>A</string>
            </attr>
        </edge>
        <edge to="n0" from="n7">
            <attr name="label">
                <string>in</string>
            </attr>
        </edge>
        <edge to="n8" from="n8">
            <attr name="label">
                <string>B</string>
            </attr>
        </edge>
        <edge to="n0" from="n8">
            <attr name="label">
                <string>in</string>
            </attr>
        </edge>
    </graph>
</gxl>
