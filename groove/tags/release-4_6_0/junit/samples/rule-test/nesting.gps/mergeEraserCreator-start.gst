<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="mergeEraserCreator-start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n138043">
            <attr name="layout">
                <string>128 190 436 21</string>
            </attr>
        </node>
        <node id="n138042">
            <attr name="layout">
                <string>290 272 18 21</string>
            </attr>
        </node>
        <node id="n138041">
            <attr name="layout">
                <string>290 125 18 21</string>
            </attr>
        </node>
        <edge to="n138043" from="n138043">
            <attr name="label">
                <string>Two node, which will be matched by both the top level A and the sublevel A</string>
            </attr>
        </edge>
        <edge to="n138042" from="n138042">
            <attr name="label">
                <string>A</string>
            </attr>
        </edge>
        <edge to="n138043" from="n138043">
            <attr name="label">
                <string>rem:</string>
            </attr>
        </edge>
        <edge to="n138042" from="n138043">
            <attr name="label">
                <string>A-node</string>
            </attr>
        </edge>
        <edge to="n138041" from="n138041">
            <attr name="label">
                <string>A</string>
            </attr>
        </edge>
        <edge to="n138041" from="n138043">
            <attr name="label">
                <string>A-node</string>
            </attr>
        </edge>
    </graph>
</gxl>
