<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n234">
            <attr name="layout">
                <string>183 217 32 18</string>
            </attr>
        </node>
        <node id="n236">
            <attr name="layout">
                <string>70 275 35 34</string>
            </attr>
        </node>
        <node id="n239">
            <attr name="layout">
                <string>68 64 37 34</string>
            </attr>
        </node>
        <node id="n235">
            <attr name="layout">
                <string>285 265 35 34</string>
            </attr>
        </node>
        <node id="n238">
            <attr name="layout">
                <string>132 177 32 18</string>
            </attr>
        </node>
        <node id="n237">
            <attr name="layout">
                <string>171 132 32 18</string>
            </attr>
        </node>
        <node id="n233">
            <attr name="layout">
                <string>214 166 32 18</string>
            </attr>
        </node>
        <node id="n240">
            <attr name="layout">
                <string>269 64 37 34</string>
            </attr>
        </node>
        <edge to="n234" from="n234">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
        <edge to="n238" from="n236">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n236" from="n236">
            <attr name="label">
                <string>type:Phil</string>
            </attr>
        </edge>
        <edge to="n234" from="n236">
            <attr name="label">
                <string>right</string>
            </attr>
        </edge>
        <edge to="n236" from="n236">
            <attr name="label">
                <string>flag:think</string>
            </attr>
        </edge>
        <edge to="n239" from="n239">
            <attr name="label">
                <string>type:Phil</string>
            </attr>
        </edge>
        <edge to="n238" from="n239">
            <attr name="label">
                <string>right</string>
            </attr>
        </edge>
        <edge to="n239" from="n239">
            <attr name="label">
                <string>flag:think</string>
            </attr>
        </edge>
        <edge to="n237" from="n239">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n233" from="n235">
            <attr name="label">
                <string>right</string>
            </attr>
        </edge>
        <edge to="n235" from="n235">
            <attr name="label">
                <string>flag:think</string>
            </attr>
        </edge>
        <edge to="n235" from="n235">
            <attr name="label">
                <string>type:Phil</string>
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
        <edge to="n237" from="n237">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
        <edge to="n233" from="n233">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
        <edge to="n240" from="n240">
            <attr name="label">
                <string>flag:think</string>
            </attr>
        </edge>
        <edge to="n240" from="n240">
            <attr name="label">
                <string>type:Phil</string>
            </attr>
        </edge>
        <edge to="n233" from="n240">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n237" from="n240">
            <attr name="label">
                <string>right</string>
            </attr>
        </edge>
    </graph>
</gxl>
